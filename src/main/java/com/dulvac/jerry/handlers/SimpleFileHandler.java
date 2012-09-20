package com.dulvac.jerry.handlers;

import org.apache.http.HttpException;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Locale;

public class SimpleFileHandler implements HttpRequestHandler {
  private static final Logger logger = LoggerFactory.getLogger(SimpleFileHandler.class.getName());
  private final String filesRoot;

  // TODO: Support more methods
  private static enum METHODS {
    GET,
    HEAD
  }

  public static String getMethod(HttpRequest request) throws MethodNotSupportedException {
    String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
    try {
      METHODS.valueOf(method);
    } catch (IllegalArgumentException ex) {
       throw new MethodNotSupportedException(method + " method not supported");
    }
    return method;
  }

  public static final StringEntity notFoundHTML = new StringEntity("<html><body><h1>File not found</h1></body></html>",
                                                                   ContentType.create("text/html", "UTF-8"));
  public static final StringEntity forbiddenHTML = new StringEntity("<html><body><h1>No! Forbidden</h1></body></html>",
                                                                    ContentType.create("text/html", "UTF-8"));

  public SimpleFileHandler(final String filesRoot) {
    super();
    this.filesRoot = filesRoot;
  }

  public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context)
    throws HttpException, IOException {

    final String method = getMethod(request);
    final String target = request.getRequestLine().getUri();
    // get client ip for logging TODO: ugly; make this lazy
    final String clientAddress = ((HttpInetConnection) context.getAttribute(ExecutionContext.HTTP_CONNECTION))
      .getRemoteAddress().getHostAddress();
    
    final File file = new File(this.filesRoot, URLDecoder.decode(target, "UTF-8"));
    if (!file.exists()) {
      response.setStatusCode(HttpStatus.SC_NOT_FOUND);
      response.setEntity(this.notFoundHTML);
      logger.info("Request from {}. File {} not found", clientAddress, file.getPath());

    } else if (!file.canRead() || file.isDirectory()) {
      response.setStatusCode(HttpStatus.SC_FORBIDDEN);
      response.setEntity(this.forbiddenHTML);
      logger.info("Request from {}. Can't read file {}", clientAddress, file.getPath());
      
    } else {
      response.setStatusCode(HttpStatus.SC_OK);
      // TODO: Support different kinds of files/ mime-types
      FileEntity body = new FileEntity(file, ContentType.create("text/html", (Charset) null));
      response.setEntity(body);
      logger.info("Request from {}. Sending file {}", clientAddress, file.getPath());
    }
  }
}
