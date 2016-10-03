package com.github.zaplatynski.firstspirit.modules.fsm.velocity;

import org.apache.velocity.app.event.IncludeEventHandler;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.util.RuntimeServicesAware;

/**
 * The type OptionalIncludeEventHandler takes care of non existing module xml files and ignores
 * them.
 */
public class OptionalIncludeEventHandler implements IncludeEventHandler, RuntimeServicesAware {

  private RuntimeServices runtimeServices;

  @Override
  public void setRuntimeServices(final RuntimeServices runtimeServices) {
    this.runtimeServices = runtimeServices;
  }

  @Override
  public String includeEvent(final String includePath, final String currentPath,
                             final String directiveName) {
    String path = includeRelative(includePath, currentPath);
    if (runtimeServices.getLoaderNameForResource(path) != null) {
      return path;
    } else {
      return null;
    }
  }

  private String includeRelative(final String includePath, final String currentPath) {
    if (!includePath.startsWith("/") && !includePath.startsWith("\\")) {
      int lastslashpos = Math.max(currentPath.lastIndexOf('/'), currentPath.lastIndexOf('\\'));
      return lastslashpos == -1 ? includePath
          : currentPath.substring(0, lastslashpos) + "/" + includePath;
    } else {
      return includePath;
    }
  }
}