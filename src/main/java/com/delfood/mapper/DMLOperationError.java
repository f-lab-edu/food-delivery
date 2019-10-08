package com.delfood.mapper;

// SUCCESS 빼고 에러발생시 아래 내용을 포함하여 throw Exception
public enum DMLOperationError {
  SUCCESS, NONE_CHANGED, TOO_MANY_CHANGED, EMPTY_RESULT
}
