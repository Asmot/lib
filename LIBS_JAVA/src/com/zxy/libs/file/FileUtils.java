package com.zxy.libs.file;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.stream.FileImageInputStream;

/**
 * 文件相关操作
 * 读写文件
 * 删除文件（夹）
 * 复制文件（夹）
 *
 */
public class FileUtils {
	
	/**
	 * 以inputStream的方式读取文件内容
	 */
	public static void readFileByByte(String path) {
		StringBuilder builder = new StringBuilder();
		
		File file = new File(path);
		
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			byte[] b = new byte[1024];
			int len = 0;
			while((len = inputStream.read(b)) > 0) {
				builder.append(new String(b,0,len));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println(builder.toString());
		
	}
	
	/**
	 * 以DataInputStream的方式读取文件内容,
	 * 它一般对应着自己的写入
	 * 它可以读取boolean，int 等等数据
	 */
	public static void readFileByDataInputStream(String path) {
		StringBuilder builder = new StringBuilder();
		
		File file = new File(path);
		
		DataInputStream inputStream = null;
		try {
			inputStream = new DataInputStream(new FileInputStream(file));
			char c ;
			// 结束表示，读到结尾会跑出 eof异常
			while(inputStream.available() > 0) {
				c = inputStream.readChar();
				builder.append(c);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println(builder.toString());
		
	}
	
	
	/**
	 * 以BufferReader的方式读取文件内容 ， 读取一行
	 */
	public static void readFileByLine(String path) {
		StringBuilder builder = new StringBuilder();
		
		File file = new File(path);
		
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			
			while(true) {
				String str = bufferedReader.readLine();
				if(str == null) {
					break;
				}
				builder.append(str);
				// add enter
				builder.append("\n");
			}
			//delete the last enter /n cost per length
			builder.deleteCharAt(builder.length() - 1);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println(builder.toString());
		
	}
	
	

	public final static int COPY_FILE_ERROE = -1;

	/**
	 * 获取一个文件或文件夹的大小
	 * 
	 * @param file
	 * @return
	 */
	public static long getFileSize(File file) {
		// Function passed a single file, return the file's length.
		if (!file.isDirectory()) {
			return file.length();
		}

		// Function passed a directory.
		// Sum and return the size of the directory's contents, including
		// subfolders.
		long netSize = 0;
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				netSize += getFileSize(f);
			} else {
				netSize += f.length();
			}
		}
		return netSize;
	}

	// ## copy

	/**
	 * 复制文件夹,带有进度回调
	 * 
	 * @param sourceLocation
	 *            源文件
	 * @param targetLocation
	 *            目标地址
	 * @param sizeOfCopiedFiles
	 *            已复制的大小
	 * @param sizeOfDirectory
	 *            总大小，需要在调用先调用getFileSize计算总大小
	 * @param listener
	 *            复制回调监听
	 * @return
	 */
	public static long copyDirectoryWithCallback(File sourceLocation,
			File targetLocation, long sizeOfCopiedFiles, long sizeOfDirectory,
			ICopyFileListener listener) {
		if (sizeOfCopiedFiles <= 0) {
			System.err
					.println("sizeOfDirectory is the total Size,  must be a positive number");
			return 0L;
		}
		String inName = sourceLocation.getName();
		String outName = targetLocation.getName();

		if (sourceLocation.isDirectory()) {
			insureDirExist(targetLocation);

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				sizeOfCopiedFiles = copyDirectoryWithCallback(new File(
						sourceLocation, children[i]), new File(targetLocation,
						children[i]), sizeOfCopiedFiles, sizeOfDirectory,
						listener);
			}
		} else {
			try {
				if (listener != null && sizeOfCopiedFiles == 0) {
					listener.onCopyStart(inName, outName);
				}
				sizeOfCopiedFiles += copyFile(sourceLocation, targetLocation);
				if (listener != null) {
					float progress = getPercent(sizeOfCopiedFiles, sizeOfDirectory);
					listener.onProgress(inName, outName, progress);
					if (progress == 1) {
						listener.onCopyFinish(inName, outName);
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
				if (listener != null) {
					listener.onCopyError(inName, outName, COPY_FILE_ERROE);
				}
			}
		}
		return sizeOfCopiedFiles;
	}

	/**
	 * 复制文件夹
	 * 
	 * @param sourceLocation
	 *            源文件
	 * @param targetLocation
	 *            目标地址
	 */
	public static void copyDirectory(File sourceLocation, File targetLocation) {
		if (sourceLocation.isDirectory()) {
			insureDirExist(targetLocation);

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(
						targetLocation, children[i]));
			}
		} else {
			try {
				copyFile(sourceLocation, targetLocation);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 复制文件
	 * 
	 * @param in
	 *            源文件
	 * @param out
	 *            目标地址
	 * @return 返回复制文件大小，返回-1 表示复制失败
	 * @throws IOException
	 */
	public static long copyFile(File in, File out) throws IOException {
		FileInputStream fis = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		long fileLength = 0L;
		// System.out.println("Copying: " + in.getAbsolutePath());
		try {
			inChannel = fis.getChannel();
			outChannel = fos.getChannel();
			if (inChannel != null) {
				inChannel.transferTo(0, inChannel.size(), outChannel);
			}
			fileLength = inChannel.size();
		} catch (Throwable e) {
			e.printStackTrace();
			return -1;
		} finally {
			if (fis != null) {
				fis.close();
			}
			if (inChannel != null) {
				inChannel.close();
			}
			if (fos != null) {
				fos.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}
		return fileLength;
	}

	// ## delete
	/**
	 * 递归求取目录文件个数
	 * 
	 * @param f
	 * @return
	 */
	public static long getTotalFileCount(File f) {
		if(f == null) {
			return 0;
		}
		if(f.isFile()) {
			return 1;
		}
		long size = 0;
		File flist[] = f.listFiles();
		if(flist == null) {
			return size;
		}
		size = size + flist.length;
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getTotalFileCount(flist[i]);
				size--;
			}
		}
		return size;
	}

	/**
	 * 递归求取目录中文件（包括目录）的个数
	 * 
	 * @param f
	 * @return
	 */
	public static long getTotalFileAndDirCount(File f) {
		if(f == null) {
			return 0;
		}
		if(f.isFile()) {
			return 1;
		}
		long size = 1;
		File flist[] = f.listFiles();
		if(flist == null) {
			return size;
		}
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getTotalFileAndDirCount(flist[i]);
			} else {
				size ++;
			}
		}
		return size;
	}

	public static void deleteFileWithCallBack(File dir,
			IDeleteFileListener listener) {
		int totalCount = (int) getTotalFileAndDirCount(dir);
		deleteFileWithCallBack(dir, totalCount, listener);
	}

	public static void deleteFileWithCallBack(File dir, int totalCount,
			IDeleteFileListener listener) {
		int deletedCount = 0;
		if (listener != null) {
			listener.onDeleteStart(dir.getAbsolutePath());
		}
		deleteFileWithCallBack(dir, deletedCount, totalCount, listener);
	}

	/**
	 * 递归删除文件(夹) 并带有回调，中间删除出现错误也不会停下来
	 * @param dir
	 * @param deletedCount
	 * @param totalCount
	 * @param listener
	 * @return
	 */
	private static int deleteFileWithCallBack(File dir, int deletedCount,
			int totalCount, IDeleteFileListener listener) {
		String name = dir.getAbsolutePath();
		if ((dir == null || !dir.exists()) && listener != null) {
			listener.onDeleteError(name);
		}
		File contents[] = dir.listFiles();
		if (contents != null) {
			for (int i = 0; i < contents.length; i++) {
				if (contents[i].isFile()) {
					String fileName = contents[i].getAbsolutePath();
					boolean flag = contents[i].delete();
//					boolean flag = true;
					deletedCount++;
					judgeToCallbackDeleteState(deletedCount, totalCount,
							listener, fileName, flag);
					if(!flag) {
//						deletedCount --;
						return deletedCount;
					}
					
				} else {
					deletedCount = deleteFileWithCallBack(contents[i],deletedCount,totalCount, listener);
				}
			}
		}
		boolean flag = dir.delete();
//		boolean flag = new Random().nextBoolean();
		deletedCount++;
		judgeToCallbackDeleteState(deletedCount, totalCount, listener, name,
				flag);
		
		if(!flag) {
//			deletedCount --;
			return deletedCount;
		}
		return deletedCount;
	}

	/**
	 * @param deletedCount
	 * @param totalCount
	 * @param listener
	 * @param name
	 * @param flag
	 */
	private static void judgeToCallbackDeleteState(int deletedCount,
			int totalCount, IDeleteFileListener listener, String name,
			boolean flag) {
//		System.out.println(deletedCount + " , " + totalCount + " flag:" + flag);
		if(listener != null) {
			if(flag) {
				listener.onDeleteSchedule(name,getPercent(deletedCount, totalCount)); 
			} else {
				listener.onDeleteError(name);
			}
			
			if(deletedCount == totalCount) {
				listener.onDeleteFinish(name);
			}
		}
	}

	/**
	 * 删除文件（夹）
	 * 
	 * @param dir
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static boolean deleteFile(File dir) throws IOException, Exception {
		if (dir == null || !dir.exists())
			return false;
		File contents[] = dir.listFiles();
		if (contents != null) {
			for (int i = 0; i < contents.length; i++) {
				if (contents[i].isFile()) {
					if (!contents[i].delete()) {
						return false;
					}
				} else {
					if (!deleteFile(contents[i])) {
						return false;
					}
				}
			}
		}
		return dir.delete();
	}

	// ## unzip
	// public static void unZip(String directory, String zipFile, int ratio) {
	// ZipInputStream zis = null;
	// FileInputStream fileinputZip = null;
	// try {
	// fileinputZip = new FileInputStream(zipFile);
	// zis = new ZipInputStream(fileinputZip);
	// File f = new File(directory);
	// insureDirExist(f);
	// fileUnZip(zis, f, ratio);
	// } catch (Throwable e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// if (fileinputZip != null) {
	// fileinputZip.close();
	// }
	// if (zis != null) {
	// zis.close();
	// }
	// } catch (IOException e) {
	// // e.printStackTrace();
	// }
	// }
	// }

	/**
	 * 获取压缩文件的大小
	 * 
	 * @param name
	 * @return
	 */
	public static long getZipFileSize(String name) {
		long totalSize = 0L;
		try {
			FileInputStream zipCountFis = new FileInputStream(name);
			CheckedInputStream zipCountCis = new CheckedInputStream(
					zipCountFis, new CRC32());

			ZipInputStream zipCountStream = new ZipInputStream(zipCountCis);
			ZipEntry entry = null;

			while ((entry = zipCountStream.getNextEntry()) != null) {
				long maxSize = entry.getSize();
				totalSize += maxSize; // 先计算出压缩后的文件总大小
				zipCountStream.closeEntry();
			}
			zipCountStream.close();
			zipCountCis.close();
			zipCountFis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return totalSize;
	}

	/**
	 * 解压文件，带有进度回调
	 * 
	 * @param zipFile
	 *            压缩文件
	 * @param directory
	 *            解压到的位置
	 * @param listener
	 *            解压回调监听
	 * @throws Exception
	 */
	public static void UnZipFileWithCallback(String zipFile, String directory,
			IUnZipListener listener) {
		ZipInputStream zis = null;
		FileInputStream fileinputZip = null;
		File file;
		long totalLength = 10L;
		if (listener != null) {
			listener.onUnZipStart(zipFile);
			totalLength = getZipFileSize(zipFile);
		}

		try {
			fileinputZip = new FileInputStream(zipFile);
			zis = new ZipInputStream(fileinputZip);

			file = new File(directory);
			insureDirExist(file);

			ZipEntry zip = zis.getNextEntry();
			long unzipedSize = 0;

			while (zip != null) {
				String name = zip.getName();
				File f = new File(file.getAbsolutePath() + "/" + name);
				if (zip.isDirectory()) {
					f.mkdirs();
				} else {
					f.createNewFile();
					FileOutputStream fos = null;
					float percent = 0;
					try {
						fos = new FileOutputStream(f);
						byte b[] = new byte[2048];
						int aa = 0;
						float prePercent = 0;
						while ((aa = zis.read(b)) != -1) {
							fos.write(b, 0, aa);
							if (listener != null) {
								unzipedSize += aa;
								percent = getPercent(unzipedSize, totalLength); // zip
								percent = (percent < 100) ? percent : 100;
								if (percent != prePercent) {
									listener.onUnzipSchedule(name, percent);
								}
								prePercent = percent;
							}

						}
					} catch (IOException e) {
						if (listener != null) {
							listener.onUnzipError(name);
						}
					} finally {
						if (fos != null) {
							fos.close();
						}
					}
				}
				zip = zis.getNextEntry();
			}

			if (listener != null) {
				listener.onUnzipFinish(zipFile);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			if (listener != null) {
				listener.onUnzipError(zipFile);
			}
		} finally {
			try {
				if (fileinputZip != null) {
					fileinputZip.close();
				}
				if (zis != null) {
					zis.close();
				}
			} catch (IOException e) {
			}
		}
	}

	// ## others
	
	/**
	 * @param counted
	 * @param totalCount
	 * @return
	 */
	private static float getPercent(int counted, int totalCount) {
		return ((float) counted / totalCount ) * 100;
	}
	
	private static float getPercent(long counted, long totalCount) {
		return ((float) counted / totalCount ) * 100;
	}

	/**
	 * 如果目录不存在，创建一个目录
	 * 
	 * @param targetLocation
	 */
	public static void insureDirExist(File dir) {
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	/**
	 * 如果目录不存在，创建一个目录
	 * 
	 * @param targetLocation
	 * @throws IOException
	 *             创建失败会抛异常
	 */
	public static void insureFileExist(File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}
	}
}
