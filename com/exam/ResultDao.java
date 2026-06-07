package com.exam;

import java.util.List;

public interface ResultDao {
    void saveResult(Result result);
    List<Result> getAllResults();
}
