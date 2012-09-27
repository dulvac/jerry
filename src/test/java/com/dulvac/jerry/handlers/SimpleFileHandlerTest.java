package com.dulvac.jerry.handlers;

import com.dulvac.jerry.TestUtils;
import com.google.common.io.Files;

import org.apache.http.HttpRequest;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.message.BasicHttpRequest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class SimpleFileHandlerTest {
  File docRoot;

  @BeforeClass
  public void oneTimeSetUp() throws IOException {
  }

  @AfterClass
  public void oneTimeTearDown() {
  }

  @BeforeMethod()
  public void setUp() throws Exception {

  }

  @AfterMethod
  public void tearDown() throws Exception {

  }

  @Test(groups={"unit"})
  public void testGetWelcomeFile() throws Exception {
    File tempRoot = Files.createTempDir();
    File withIndexDir = new File(tempRoot, "with_index");
    withIndexDir.mkdir();
    File index = new File(withIndexDir, "index.html");
    index.createNewFile();
    File withoutIndexDir = new File(tempRoot, "without_index");
    withoutIndexDir.mkdir();

    Assert.assertEquals(SimpleFileHandler.getWelcomeFile(withIndexDir), index);
    Assert.assertEquals(SimpleFileHandler.getWelcomeFile(withoutIndexDir), withoutIndexDir);
  }

  @Test(groups={"unit"})
  public void testGetMethodValid() throws Exception {
    HttpRequest request = new BasicHttpRequest("GET", "/");
    Assert.assertEquals(SimpleFileHandler.getMethod(request), "GET");

    request = new BasicHttpRequest("HEAD", "/some_resource");
    Assert.assertEquals(SimpleFileHandler.getMethod(request), "HEAD");
  }

  @Test(groups={"unit"}, expectedExceptions = MethodNotSupportedException.class)
  public void testGetMethodInvalid() throws Exception {
    HttpRequest request = new BasicHttpRequest("IDONTEXIST", "/"); // The constructor doesn't fail
    String method = SimpleFileHandler.getMethod(request); //this should throw exception
  }
}
