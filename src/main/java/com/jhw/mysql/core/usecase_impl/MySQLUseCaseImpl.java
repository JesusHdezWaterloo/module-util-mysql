package com.jhw.mysql.core.usecase_impl;

import com.clean.core.app.services.ExceptionHandler;
import com.clean.core.app.services.Notification;
import com.clean.core.app.services.NotificationsGeneralType;
import com.clean.core.app.usecase.DefaultReadWriteUseCase;
import com.clean.core.domain.services.Resource;
import com.jhw.mysql.core.domain.Configuration;
import com.jhw.mysql.core.module.MySQLCoreModule;
import javax.inject.Inject;
import com.jhw.mysql.core.repo_def.MySQLRepo;
import com.jhw.mysql.core.usecase_def.MySQLUseCase;
import java.io.File;
import java.util.List;

public class MySQLUseCaseImpl extends DefaultReadWriteUseCase<Configuration> implements MySQLUseCase {

    public static final String NOTIFICATION_SALVA_DB = "notification.mysql.saved";
    public static final String MSG_SAVED = "msg.mysql.success.saved_db";
    public static final String MSG_NO_SAVED = "msg.mysql.error.no_save";
    public static final String MSG_STARTED = "msg.mysql.success.started";
    public static final String MSG_NO_STARTED = "msg.mysql.error.no_start";
    public static final String MSG_CLOSED = "msg.mysql.success.closed";
    public static final String MSG_NO_CLOSED = "msg.mysql.error.no_close";

    /**
     * Instancia del repo para almacenar las cosas en memoria
     */
    private final MySQLRepo repo = MySQLCoreModule.getInstance().getImplementation(MySQLRepo.class);

    /**
     * Constructor por defecto, usado par injectar.
     */
    @Inject
    public MySQLUseCaseImpl() {
        super.setRepo(repo);
    }

    @Override
    public void save(String DB_name, String... tables) {
        try {
            Configuration cfg = read();
            File folder = new File(new File("").getAbsolutePath() + File.separator + cfg.getDbSaveFolder());
            folder.mkdirs();

            String exportCmd = (cfg.getBatchFolder() + File.separator + "mysql" + File.separator + "bin" + File.separator).replace(" ", "\" \"");

            exportCmd += "mysqldump -u " + cfg.getUser();
            if (!cfg.getPass().isEmpty()) {
                exportCmd += " -p " + cfg.getPass();
            }
            exportCmd += " --port " + cfg.getPort() + " -d " + DB_name + " ";
            for (String t : tables) {
                exportCmd += t + " ";
            }
            exportCmd += "--no-data=FALSE --extended-insert=FALSE > ";
            exportCmd += (folder.getAbsolutePath() + File.separator + File.separator + DB_name + System.currentTimeMillis() + ".sql").replace(" ", "\" \"");

            int resp = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", exportCmd}).waitFor();
            if (resp == 0) {
                Notification.showNotification(NOTIFICATION_SALVA_DB,
                        Resource.getString(MSG_SAVED));
            }
        } catch (Exception e) {
            Exception ex = new Exception(Resource.getString(MSG_NO_SAVED));
            ex.setStackTrace(e.getStackTrace());
            ExceptionHandler.handleException(ex);
        }
    }

    @Override
    public void start() {
        try {
            Configuration cfg = read();
            if (cfg.isStartMysqlService()) {//inicia mysql
                String cmd = "start /B " + cfg.getBatchFolder() + File.separator + "mysql_start.bat";
                int resp = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", cmd}).waitFor();
                if (resp == 0) {
                    Notification.showNotification(NotificationsGeneralType.NOTIFICATION_SUCCESS,
                            Resource.getString(MSG_STARTED));
                }
            }
        } catch (Exception e) {
            Exception ex = new Exception(Resource.getString(MSG_NO_STARTED));
            ex.setStackTrace(e.getStackTrace());
            ExceptionHandler.handleException(ex);
        }
    }

    @Override
    public void close() {
        try {
            Configuration cfg = read();
            if (cfg.isStartMysqlService()) {//inicia mysql
                String cmd = "start /B " + cfg.getBatchFolder() + File.separator + "mysql_stop.bat";
                int resp = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", cmd}).waitFor();
                if (resp == 0) {
                    Notification.showNotification(NotificationsGeneralType.NOTIFICATION_SUCCESS,
                            Resource.getString(MSG_CLOSED));
                }
            }
        } catch (Exception e) {
            Exception ex = new Exception(Resource.getString(MSG_NO_CLOSED));
            ex.setStackTrace(e.getStackTrace());
            ExceptionHandler.handleException(ex);
        }
    }

    @Override
    public void update(List<String> sqlToRun) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
