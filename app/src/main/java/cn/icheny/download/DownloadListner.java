package cn.icheny.download;

/**
 * 下载监听
 *
 * @author ocean
 */
public interface DownloadListner {
    void onFinished();

    void onProgress(float progress);

    void onPause();

    void onCancel();
}
