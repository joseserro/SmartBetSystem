package main.java.tsi.viewer;

import main.java.tsi.gui.Gui;

import org.opt4j.core.optimizer.Control;
import org.opt4j.core.optimizer.Optimizer;
import org.opt4j.core.optimizer.OptimizerStateListener;
import org.opt4j.core.start.Constant;
import org.opt4j.viewer.*;

import com.google.inject.Inject;

/**
 * The {@link Viewer}.
 * 
 * @author lukasiewycz
 * 
 */
public class ViewerNovo implements OptimizerStateListener {
	
		protected final Control control;

		protected final boolean closeOnStop;

		protected final String title;

		protected final Viewport viewport;

		protected final ToolBar toolBar;

		protected final StatusBarNovo statusBar;

		//protected JFrame frame = null;

		@Inject
		public ViewerNovo(Viewport viewport, ToolBar toolBar, StatusBarNovo statusBar, Control control,
						@Constant(value = "title", namespace = ViewerNovo.class) String title,
						@Constant(value = "closeOnStop", namespace = ViewerNovo.class) boolean closeOnStop) {
				this.viewport = viewport;
				this.toolBar = toolBar;
				this.statusBar = statusBar;
				this.title = title;
				this.control = control;
				this.closeOnStop = closeOnStop;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.opt4j.core.optimizer.OptimizerStateListener#optimizationStarted(org
		 * .opt4j.core.optimizer.Optimizer)
		 */
		@Override
		public void optimizationStarted(Optimizer optimizer) {

				statusBar.init();

				Gui.setStatusBar(statusBar);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.opt4j.core.optimizer.OptimizerStateListener#optimizationStopped(org
		 * .opt4j.core.optimizer.Optimizer)
		 */
		@Override
		public void optimizationStopped(Optimizer optimizer) {
				
		}
}

