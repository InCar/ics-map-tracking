package com.incarcloud.base.db.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowHandler<R> {
    R apply(ResultSet rs) throws SQLException;
}
