package com.amap.gbl.sdkdemo.map;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.amap.gbl.sdkdemo.R;
import com.autonavi.gbl.map.GLMapView;
import com.autonavi.gbl.map.gloverlay.BaseMapOverlay;
import com.autonavi.gbl.map.gloverlay.GLMarker;
import com.autonavi.gbl.map.gloverlay.GLPointOverlay;
import com.autonavi.gbl.map.gloverlay.GLTextureProperty;

public class PointOverlay extends BaseMapOverlay<GLPointOverlay, Object> {

    public static final String TAG = PointOverlay.class.getSimpleName();

    public PointOverlay(int engineID, Context context, GLMapView mapView) {
        super(engineID, context, mapView);
    }

    @Override
    protected void iniGLOverlay() {
        mGLOverlay = new GLPointOverlay(mEngineID, mMapView, hashCode());
        mGLOverlay.setAnimatorType(GLMarker.ANIMATOR_FALL);
        resumeMarker();
    }

    @Override
    public void addItem(Object item) {
        Log.i(TAG, "addItem: ");
        mGLOverlay.addPointOverlayItem(221010004, 101712921, R.drawable.b_child_poi_hl);
    }


    @Override
    public void resumeMarker() {
        addOverlayTexture(R.drawable.b_child_poi_hl, GLMarker.AG_ANCHOR_CENTER_BOTTOM);
    }

    private void addOverlayTexture(int resId, int anchor) {
        GLTextureProperty glTextureProperty = new GLTextureProperty();
        glTextureProperty.mId = resId;
        glTextureProperty.mAnchor = anchor;
        glTextureProperty.mBitmap = BitmapFactory.decodeResource(mMapView.getResources(), resId);
        glTextureProperty.mXRatio = 0;
        glTextureProperty.mYRatio = 0;
        glTextureProperty.isGenMimps = false;
        mMapView.addOverlayTexture(mEngineID, glTextureProperty);
    }

    public void addItem(int x, int y){
        mGLOverlay.addPointOverlayItem(x, y, R.drawable.b_child_poi_hl);
    }


    public void removeOverlay(){
        mGLOverlay.removeAll();
    }
}
