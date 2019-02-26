package com;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class SugarDaoGenerator {
    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(3, "com.sugar");

        Entity entry = schema.addEntity("Entry");
        entry.addIdProperty();
        entry.addStringProperty("description");
        entry.addDoubleProperty("sugar_amount");
        entry.addLongProperty("date");

        new DaoGenerator().generateAll(schema, "../../app/src-gen");
    }
}
