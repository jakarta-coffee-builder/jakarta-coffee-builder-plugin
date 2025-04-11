package com.apuntesdejava.jakartacoffeebuilder;

import org.apache.maven.plugin.logging.Log;

public class LogTest implements Log {

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(CharSequence charSequence) {

    }

    @Override
    public void debug(CharSequence charSequence, Throwable throwable) {

    }

    @Override
    public void debug(Throwable throwable) {

    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(CharSequence charSequence) {

    }

    @Override
    public void info(CharSequence charSequence, Throwable throwable) {

    }

    @Override
    public void info(Throwable throwable) {

    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(CharSequence charSequence) {

    }

    @Override
    public void warn(CharSequence charSequence, Throwable throwable) {

    }

    @Override
    public void warn(Throwable throwable) {

    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(CharSequence charSequence) {

    }

    @Override
    public void error(CharSequence charSequence, Throwable throwable) {

    }

    @Override
    public void error(Throwable throwable) {

    }
}
