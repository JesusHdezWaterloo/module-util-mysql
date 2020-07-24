package com.jhw.mysql.repo.module;

import com.jhw.mysql.repo.repo_impl.MySQLRepoImpl;
import com.google.inject.AbstractModule;
import com.jhw.mysql.core.repo_def.MySQLRepo;

/**
 * Configuracion del injection del modulo de licencia-repo.
 *
 * @author Jesus Hernandez Barrios (jhernandezb96@gmail.com)
 */
public class InjectionConfigMySQLRepo extends AbstractModule {

    @Override
    protected void configure() {
        bind(MySQLRepo.class).to(MySQLRepoImpl.class);
    }

}
