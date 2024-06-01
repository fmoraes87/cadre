package com.cadre.server.core.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.persistence.jdbc.Trx;

public abstract class CadreServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CadreServlet.class);


    @Override
    public void init() {
        LOGGER.info("Initializing {}", CadreServlet.class);
    }

	@Override
	protected void service(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {

		Trx trx = Trx.get(Trx.createTrxName(), true);

		try {
			CadreEnv.setContextValue("#LOCAL_TRX_NAME", trx.getTrxName());

			processComponentRequest(req, resp);

		} catch (Throwable ex) {
			if (trx != null) {
				trx.rollback();
				trx.close();
				trx = null;
			}
			
			LOGGER.error(ex.getMessage(), ex);

		}finally {
			if (trx != null) {
				trx.commit();
				trx.close();
			}
		}

	}

	protected abstract void processComponentRequest(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException;

}
