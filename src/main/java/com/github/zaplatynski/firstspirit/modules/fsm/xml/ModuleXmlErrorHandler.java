package com.github.zaplatynski.firstspirit.modules.fsm.xml;

import org.apache.maven.plugin.logging.Log;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * The type Module xml error handler.
 */
public class ModuleXmlErrorHandler implements ErrorHandler {

  private final Log log;

  /**
   * Instantiates a new Module xml error handler.
   *
   * @param log the log
   */
  public ModuleXmlErrorHandler(final Log log) {
    this.log = log;
  }

  @Override
  public void warning(final SAXParseException exception) throws SAXException {
    log.warn("XML WARNING! " + exception.toString());
  }

  @Override
  public void error(final SAXParseException exception) throws SAXException {
    log.error("XML ERROR! " + exception.getLocalizedMessage(), exception);
    throw exception;
  }

  @Override
  public void fatalError(final SAXParseException exception) throws SAXException {
    log.error("FATAL XML ERROR! " + exception.getLocalizedMessage(), exception);
    throw exception;
  }
}
