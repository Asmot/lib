package com.zxy.libs.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class FileUtils {

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
			FileCopyListener listener) {
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
					float progress = ((float) sizeOfCopiedFiles / sizeOfDirectory);
					listener.onProgress(inName, outName, progress * 100);
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
	
	private static class Counter {
		private int count = 0;
		public void add() {
			count ++;
		}
		public int getCount() {
			return count;
		}
	}

	public static void deleteFileWithCallBack(File dir,
			FileDeleteListener listener) {
		int totalCount = (int) getTotalFileAndDirCount(dir);
		deleteFileWithCallBack(dir, totalCount, listener);
	}

	public static void deleteFileWithCallBack(File dir, int totalCount,
			FileDeleteListener listener) {
		Counter deletedCount = new Counter();
		if (listener != null) {
			listener.onDeleteStart(dir.getAbsolutePath());
		}
		deleteFileWithCallBack(dir, deletedCount, totalCount, listener);
	}

	private static boolean deleteFileWithCallBack(File dir, Counter deletedCount,
			int totalCount, FileDeleteListener listener) {
		String name = dir.getAbsolutePath();
		if ((dir == null || !dir.exists()) && listener != null) {
			listener.onDeleteError(name);
		}
		File contents[] = dir.listFiles();
		if (contents != null) {
			for (int i = 0; i < contents.length; i++) {
				if (contents[i].isFile()) {
					String fileName = contents[i].getAbsolutePath();
//					boolean flag = contents[i].delete();
					boolean flag = true;
					deletedCount.add();
//					deletedCount++;
					judgeToCallbackDeleteState(deletedCount.getCount(), totalCount,
							listener, fileName, flag);
					if(!flag) {
						return false;
					}
					
				} else {
					if(!deleteFileWithCallBack(contents[i],deletedCount,totalCount, listener)) {
						if (listener != null) {
							listener.onDeleteError(name);
						}
						return false;
					}
				}
			}
		}
//		boolean flag = dir.delete();
		boolean flag = true;
//		deletedCount++;
		deletedCount.add();
		judgeToCallbackDeleteState(deletedCount.getCount(), totalCount, listener, name,
				flag);
		if(!flag) {
			return false;
		}
		if(deletedCount.getCount() == totalCount) {
			listener.onDeleteFinish(name);
		}
		return true;
	}

	/**
	 * @param deletedCount
	 * @param totalCount
	 * @param listener
	 * @param name
	 * @param flag
	 */
	private static void judgeToCallbackDeleteState(int deletedCount,
			int totalCount, FileDeleteListener listener, String name,
			boolean flag) {
		System.out.println(deletedCount + " , " + totalCount + " flag:" + flag);
		if(listener != null) {
			if(flag) {
				listener.onDeleteSchedule(name,((float) deletedCount / totalCount ) * 100); 
			} else {
				listener.onDeleteError(name);
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
			FileUnZipListener listener) {
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
								percent = ((float) unzipedSize / totalLength) * 100; // zip
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
