package io.openex.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.stereotype.Component;

import java.sql.Statement;

@Component
public class V2_55__Update_communication extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Statement select = context.getConnection().createStatement();
        // Add new required fields
        select.execute("ALTER TABLE communications ADD communication_attachments text[];");
    }
}
