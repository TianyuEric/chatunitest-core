package zju.cst.aces.api;

import zju.cst.aces.dto.MethodInfo;

/**
 * Runner is an interface to run code.
 */
public interface Runner {

    public void runClass(String fullClassName);

    public void runMethod(String fullClassName, MethodInfo methodInfo);
}