package org.anhtran.wakeup.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by anhtran on 3/14/17.
 */

public final class AlarmContract {

    public static final String CONTENT_AUTHORITY = "org.anhtran.wakeup";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ALARMS = "alarms";

    private AlarmContract() {
    }

    public static final class AlarmEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ALARMS);

        public static final String TABLE_NAME = "alarms";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_ALARM_NAME = "name";

        public static final String COLUMN_ALARM_DATETIME = "datetime";

        public static final String COLUMN_ALARM_STATE = "state";

        public static final int STATE_ON = 1;

        public static final int STATE_OFF = 0;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of alarms.
         */

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_ALARMS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single alarm.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_ALARMS;

        public static boolean isStateValid(int state) {
            return state == STATE_OFF || state == STATE_ON;
        }

    }

}
