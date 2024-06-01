package com.cadre.server.core.event;

import com.cadre.server.core.boundary.ServiceProvider;

public interface EventHandler extends ServiceProvider {

	void handleEvent(CEvent event);

}
