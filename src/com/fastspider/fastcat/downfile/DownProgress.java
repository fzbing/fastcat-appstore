package com.fastspider.fastcat.downfile;

public interface DownProgress {
    void onStart(long length);

    void onUpdate(long count);

    void onEnd();

}
