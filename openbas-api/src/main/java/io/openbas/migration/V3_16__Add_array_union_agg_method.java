package io.openbas.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.Statement;

@Component
public class V3_16__Add_array_union_agg_method extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {
    Connection connection = context.getConnection();
    Statement select = connection.createStatement();
    select.execute("CREATE FUNCTION array_union(a ANYARRAY, b ANYARRAY)"
        + "    RETURNS ANYARRAY AS"
        + "    $$"
        + "SELECT array_agg(DISTINCT x)"
        + "FROM ("
        + "         SELECT unnest(a) x"
        + "         UNION ALL SELECT unnest(b)"
        + "     ) AS u"
        + "    $$ LANGUAGE SQL;"
        + "CREATE AGGREGATE array_union_agg(ANYARRAY) ("
        + "  SFUNC = array_union,"
        + "  STYPE = ANYARRAY,"
        + "  INITCOND = '{}'"
        + ");");
  }
}