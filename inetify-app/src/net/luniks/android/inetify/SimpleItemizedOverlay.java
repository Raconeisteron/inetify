/*
 * Copyright 2011 Torsten Römer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.luniks.android.inetify;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * Basic implementation of ItemizedOverlay.
 * 
 * @author torsten.roemer@luniks.net
 */
public class SimpleItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	@SuppressWarnings("unused")
	private final int textSize;
	private final boolean shadow;
	
	private final ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();

	/**
	 * Creates an instance showing the given drawable as marker with a shadow or not.
	 * @param defaultMarker
	 * @param textSize currently not used
	 * @param shadow
	 */
	public SimpleItemizedOverlay(final Drawable defaultMarker, final int textSize, final boolean shadow) {
		super(boundCenter(defaultMarker));
		this.textSize = textSize;
		this.shadow = shadow;
	}

	public void addOverlay(final OverlayItem overlayItem) {
		overlayItems.add(overlayItem);
	    populate();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, this.shadow);
		
		/*
		for(OverlayItem overlayItem : overlayItems) {
			GeoPoint geoPoint = overlayItem.getPoint();
	        Point screenPoint = new Point() ;
	        mapView.getProjection().toPixels(geoPoint, screenPoint);
			
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setAntiAlias(true);
			paint.setTextSize(textSize);
			paint.setTextAlign(Align.CENTER);
			canvas.drawText(overlayItem.getTitle(), screenPoint.x, screenPoint.y, paint);
		}
		*/
	}

	@Override
	protected OverlayItem createItem(final int item) {
	  return overlayItems.get(item);
	}

	@Override
	public int size() {
		return overlayItems.size();
	}

}
