package com.basecamp.turbolinks;

import android.webkit.WebViewClient;

/**
 * <p>Defines callbacks that Turbolinks makes available to your app. This interface is required, and
 * should be implemented in an activity (or similar class).</p>
 *
 * <p>Often these callbacks handle error conditions, but there are also some convenient timing events
 * where you can do things like routing, inject custom Javascript, etc.</p>
 */
public interface TurbolinksAdapter {
    /**
     * <p>Called after the Turbolinks Javascript bridge has been injected into the webView, during the
     * Android WebViewClient's standard onPageFinished callback.
     */
    void onPageFinished();

    /**
     * <p>Called when the Android WebViewClient's standard onReceivedError callback is fired.</p>
     *
     * @param errorCode Passed through error code returned by the Android WebViewClient.
     */
    void onReceivedError(int errorCode);

    /**
     * <p>Called when Turbolinks detects that the page being visited has been invalidated, typically
     * by new resources in the the page HEAD.</p>
     */
    void pageInvalidated();

    /**
     *<p>Called when Turbolinks receives an HTTP error from a Turbolinks request.</p>
     *
     * @param statusCode HTTP status code returned by the request.
     */
    void requestFailedWithStatusCode(int statusCode);

    /**
     * <p>Called when Turbolinks considers the visit fully completed -- the request fulfilled
     * successfully and page rendered.</p>
     */
    void visitCompleted();

    /**
     * <p>Called when Turbolinks first starts a visit, typically from a link inside a webView.</p>
     *
     * @param location URL to be visited.
     * @param action Whether to treat the request as an advance (navigating forward) or a replace (back).
     */
    void visitProposedToLocationWithAction(String location, String action, String target);

    /**
     * <p>Called by TurbolinksWebClient after some Turbolinks processing.</p>
     *
     * @see TurbolinksWebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
     * @see WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
     *
     * @param url The URL to be loaded
     * @return true if the host application wants to handle the URL itself, otherwise return false
     */
    boolean shouldOverrideUrlLoading(String url);

    /**
     * <p>Take action after a URL is loaded in an active WebView.</p>
     *
     * @see com.basecamp.turbolinks.TurbolinksWebViewClient#onLoadResource(android.webkit.WebView, java.lang.String)
     * @see WebViewClient#onLoadResource(android.webkit.WebView, java.lang.String)
     *
     * @param url
     */
    void onLoadResource(String url);
}
