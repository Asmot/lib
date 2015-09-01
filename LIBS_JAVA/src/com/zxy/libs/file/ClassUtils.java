package com.zxy.libs.file;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtils {

	/**
	 * ��ȡĳһ�����µ������� �����Ǳ���õ�class�ļ�Ҳ������jar��
	 * 
	 * @param pkgName
	 */
	public static List<Class<?>> getClasses(String pkgName) {

		List<Class<?>> classes = new ArrayList<>();

		// �������е�'.'����'/'
		pkgName = pkgName.replace(".", "/");

		// �Ƿ�ѭ������
		boolean recursive = true;

		Enumeration<URL> dirs;

		try {
			dirs = Thread.currentThread().getContextClassLoader()
					.getResources(pkgName);

			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();

				// ��jar��file���� Ӧ�þ��Ǳ����ļ�������
				String protocol = url.getProtocol();
				if (protocol.equals("file")) {
					// ��ȡ�����ַ
					String filePath = URLDecoder.decode(url.getFile(), "utf-8");

					// Ȼ����ļ��ж�ȡ��
					fetchAllClassesInFile(pkgName, filePath, true, classes);
				} else if (protocol.equals("jar")) {
					// �����jar���ļ�
					// ����һ��JarFile
					JarFile jar;
					try {
						// ��ȡjar
						jar = ((JarURLConnection) url.openConnection())
								.getJarFile();
						// �Ӵ�jar�� �õ�һ��ö����
						Enumeration<JarEntry> entries = jar.entries();
						// ͬ���Ľ���ѭ������
						while (entries.hasMoreElements()) {
							// ��ȡjar���һ��ʵ�� ������Ŀ¼ ��һЩjar����������ļ� ��META-INF���ļ�
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							// �������/��ͷ��
							if (name.charAt(0) == '/') {
								// ��ȡ������ַ���
								name = name.substring(1);
							}
							// ���ǰ�벿�ֺͶ���İ�����ͬ
							if (name.startsWith(pkgName)) {
								int idx = name.lastIndexOf('/');
								// �����"/"��β ��һ����
								if (idx != -1) {
									// ��ȡ���� ��"/"�滻��"."
									pkgName = name.substring(0, idx).replace(
											'/', '.');
								}
								// ������Ե�����ȥ ������һ����
								if ((idx != -1) || recursive) {
									// �����һ��.class�ļ� ���Ҳ���Ŀ¼
									if (name.endsWith(".class")
											&& !entry.isDirectory()) {
										// ȥ�������".class" ��ȡ����������
										String className = name.substring(
												pkgName.length() + 1,
												name.length() - 6);
										try {
											// ��ӵ�classes
											classes.add(Class.forName(pkgName
													+ '.' + className));
										} catch (ClassNotFoundException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return classes;
	}

	/**
	 * ���ļ�����ʽ����ȡ���µ�����Class
	 * 
	 * @param packageName
	 *            ����
	 * @param packagePath
	 *            �ļ�·��
	 * @param recursive
	 *            true��ʾ�ݹ��ѯ
	 * @param classes
	 *            ���class��list
	 */
	public static void fetchAllClassesInFile(String packageName,
			String packagePath, final boolean recursive, List<Class<?>> classes) {
		// ��ȡ�˰���Ŀ¼ ����һ��File
		File dir = new File(packagePath);
		// ��������ڻ��� Ҳ����Ŀ¼��ֱ�ӷ���
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		// ������� �ͻ�ȡ���µ������ļ� ����Ŀ¼
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// �Զ�����˹��� �������ѭ��(������Ŀ¼) ��������.class��β���ļ�(����õ�java���ļ�)
			public boolean accept(File file) {
				return (recursive && file.isDirectory())
						|| (file.getName().endsWith(".class"));
			}
		});
		// ѭ�������ļ�
		for (File file : dirfiles) {
			// �����Ŀ¼ �����ɨ��
			if (file.isDirectory()) {
				fetchAllClassesInFile(packageName + "." + file.getName(),
						file.getAbsolutePath(), recursive, classes);
			} else {
				// �����java���ļ� ȥ�������.class ֻ��������
				String className = file.getName().substring(0,
						file.getName().length() - 6);
				try {
					// ��ӵ�������ȥ
					// Class.forName��ʱ����ʽΪ xx.xx.xx ���� xx/xx/xx
					String tmpPkgName = packageName.replace("/", ".");
					classes.add(Class.forName(tmpPkgName + '.' + className));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ȡ��ĳ���ӿ�������ʵ������ӿڵ���
	 * */
	public static List<Class> getAllClassByInterface(Class c) {
		List<Class> returnClassList = null;

		if (c.isInterface()) {
			// ��ȡ��ǰ�İ���
			String packageName = c.getPackage().getName();
			// ��ȡ��ǰ�����Լ��Ӱ������Ե���
			List<Class<?>> allClass = getClasses(packageName);
			if (allClass != null) {
				returnClassList = new ArrayList<Class>();
				for (Class classes : allClass) {
					// �ж��Ƿ���ͬһ���ӿ�
					if (c.isAssignableFrom(classes)) {
						// ���������ȥ
						if (!c.equals(classes)) {
							returnClassList.add(classes);
						}
					}
				}
			}
		}

		return returnClassList;
	}

}
