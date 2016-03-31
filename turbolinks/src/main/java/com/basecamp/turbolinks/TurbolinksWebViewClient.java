package com.basecamp.turbolinks;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Date;

public class TurbolinksWebViewClient extends WebViewClient {
    private final TurbolinksSession session;

    TurbolinksWebViewClient(TurbolinksSession session) {
        this.session = session;
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
        session.turbolinksAdapter.onLoadResource(url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        session.coldBootInProgress = true;
    }

    @Override
    public void onPageFinished(WebView view, String location) {
        if (!session.turbolinksBridgeInjected) {
            TurbolinksJavascriptInjector.injectTurbolinksBridge(session, session.applicationContext, session.webView);
            session.turbolinksAdapter.onPageFinished();

            TurbolinksLog.d("Page finished: " + location);
        }
    }

    /**
     * Turbolinks will not call adapter.visitProposedToLocationWithAction in some cases,
     * like target=_blank or when the domain doesn't match. We still route those here.
     * This is mainly only called when links within a webView are clicked and not during
     * loadUrl. However, a redirect on a cold boot can also cause this to fire, so don't
     * override in that situation, since Turbolinks is not yet ready.
     * http://stackoverflow.com/a/6739042/3280911
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (session.turbolinksAdapter.shouldOverrideUrlLoading(url)) return true;

        if (!session.turbolinksIsReady || session.coldBootInProgress) {
            return false;
        }

        /**
         * Prevents firing twice in a row within a few milliseconds of each other, which
         * happens. So we check for a slight delay between requests, which is plenty of time
         * to allow for a user to click the same link again.
         */
        long currentOverrideTime = new Date().getTime();
        if ((currentOverrideTime - session.previousOverrideTime) > 500) {
            session.previousOverrideTime = currentOverrideTime;
            TurbolinksLog.d("Overriding load: " + url);
            session.visitProposedToLocationWithAction(url, TurbolinksSession.ACTION_ADVANCE, null);
        }

        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        session.resetToColdBoot();

        session.turbolinksAdapter.onReceivedError(errorCode);
        TurbolinksLog.d("onReceivedError: " + errorCode);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);

        if (request.isForMainFrame()) {
            session.resetToColdBoot();
            session.turbolinksAdapter.onReceivedError(errorResponse.getStatusCode());
            TurbolinksLog.d("onReceivedHttpError: " + errorResponse.getStatusCode());
        }
    }
}
