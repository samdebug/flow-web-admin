package com.yzx.flow.common.exception;

public class MyException
extends RuntimeException
{
private static final long serialVersionUID = 7718073001503111829L;

public MyException() {}

public MyException(String message)
{
  super(message);
}

public MyException(Throwable cause)
{
  super(cause);
}

public MyException(String message, Throwable cause)
{
  super(message, cause);
}
}