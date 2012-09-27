package com.dulvac.jerry;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import java.io.File;
import java.io.IOException;

public class TestUtils {
  private static final String WWW_DIR = "www";
  private static final String SMALL_FILES_DIR = "small";
  private static final String LARGE_FILES_DIR = "large";

  /**
   * <p>Creates a String of size {@param nrOfCharacters} containing random ASCII characters </p>
   *
   * @param nrOfCharacters Nr of characters to generate
   * @return A string containing the file content
   * @throws IllegalArgumentException if nrOfCharacters is negative
   */
  public static String generateFileContent(int nrOfCharacters) throws IllegalArgumentException {

    if (nrOfCharacters < 0) throw new IllegalArgumentException("Number of characters must be positive.");

    if (nrOfCharacters < 50) {
      return RandomStringUtils.randomAscii(nrOfCharacters);
    }

    StringBuilder sb = new StringBuilder(nrOfCharacters + 1);
    sb.append(String.format("<html><body><h1>This page has %,d characters</h1>", nrOfCharacters));
    String closeTag = "</body></html>";
    int size = sb.length() + closeTag.length();
    sb.append(RandomStringUtils.randomAscii(nrOfCharacters - size)); // This uses a buffer internally
    sb.append(closeTag);

    return sb.toString();
  }

  /**
   * <p>Creates a temporary directory containing the document root for the server instance used in tests</p>
   * <p>Also creates different convenience html files</p>
   *
   * @return a reference to a {@link File object} representing a temporary directory
   * @throws IOException
   */
  public static File createTempRootDirectoryDemo() throws IOException {
    // Create temporary directory to store test files
    File tempRoot = Files.createTempDir();

    // Create document root
    File wwwDir = new File(tempRoot, WWW_DIR);
    wwwDir.mkdir();

    // Create index file
    File index = new File(wwwDir, "index.html");
    index.createNewFile();
    Files.write("<html><body><h1>Test</h1></body><html>", index, Charsets.UTF_8);

    // Create directory with small and large files
    File smallFilesDir = new File(wwwDir, SMALL_FILES_DIR);
    File largeFilesDir = new File(wwwDir, LARGE_FILES_DIR);
    smallFilesDir.mkdir();
    largeFilesDir.mkdir();

    // Create some small files
    for (int i = 0; i < 5; i++) {
      File small = new File(smallFilesDir, "small" + i);
      small.createNewFile();
      Files.write(generateFileContent(50 + RandomUtils.nextInt(200)), small, Charsets.UTF_8);
    }

    // Create some large files
    for (int i = 0; i < 2; i++) {
      File large = new File(largeFilesDir, "large" + i);
      large.createNewFile();
      Files.write(generateFileContent((int) 5E6 * (i + 1) + RandomUtils.nextInt((int) 5E6)), large,
                  Charsets.UTF_8); // 5-10 and 10-15 MB
    }

    return tempRoot;
  }
}
