package main.java.tsi.viewer;

import org.opt4j.viewer.*;

import org.opt4j.core.config.Icons;
import org.opt4j.core.config.annotations.Icon;
import org.opt4j.core.config.annotations.Info;
import org.opt4j.core.start.Constant;
import org.opt4j.core.start.Opt4J;
import org.opt4j.core.start.Progress;
import org.opt4j.viewer.Viewer.CloseEvent;


/**
 * The {@link ViewerModule} configures the optimization process main.java.tsi.viewer.
 * 
 * @author lukasiewycz
 * 
 */
@Icon(Icons.APPLICATION)
@Info("A graphical main.java.tsi.viewer to observe the optimization process.")
public class ViewerNovoModule extends VisualizationModule {

		@Info("The title of the main.java.tsi.viewer frame.")
		@Constant(value = "title", namespace = ViewerNovo.class)
		protected String title = "Opt4J " + Opt4J.getVersion() + " Viewer";

		@Info("Event for the optimization process if the main.java.tsi.viewer is closed.")
		@Constant(value = "closeEvent", namespace = ViewerNovo.class)
		protected CloseEvent closeEvent = CloseEvent.STOP;

		@Info("Close main.java.tsi.viewer automatically when the optimization process stops.")
		@Constant(value = "closeOnStop", namespace = ViewerNovo.class)
		protected boolean closeOnStop = false;

		/**
		 * Returns {@code true} if the GUI is automatically closed if the
		 * optimization process has stopped.
		 * 
		 * @return {@code true} if the GUI is automatically closed on optimization
		 *         stop
		 */
		public boolean isCloseOnStop() {
				return closeOnStop;
		}

		/**
		 * Sets the option for automatically closing the GUI if the optimization
		 * stops.
		 * 
		 * @param closeOnStop
		 *            the closeOnStop to set
		 */
		public void setCloseOnStop(boolean closeOnStop) {
				this.closeOnStop = closeOnStop;
		}

		/**
		 * Returns the event when the GUI is closed.
		 * 
		 * @return the closeEvent
		 */
		public CloseEvent getCloseEvent() {
				return closeEvent;
		}

		/**
		 * Sets the event when the GUI is closed.
		 * 
		 * @param closeEvent
		 *            the closeEvent to set
		 */
		public void setCloseEvent(CloseEvent closeEvent) {
				this.closeEvent = closeEvent;
		}

		/**
		 * Returns the title of the GUI frame.
		 * 
		 * @return the title of the GUI frame
		 */
		public String getTitle() {
				return title;
		}

		/**
		 * Sets the title of the GUI frame.
		 * 
		 * @param title
		 *            the title of the GUI frame
		 */
		public void setTitle(String title) {
				this.title = title;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.inject.AbstractModule#configure()
		 */
		@Override
		public void config() {
				bind(ViewerNovo.class).in(SINGLETON);
				addOptimizerStateListener(ViewerNovo.class);

				bind(StatusBarNovo.class).in(SINGLETON);
				addOptimizerStateListener(StatusBarNovo.class);
				addOptimizerIterationListener(StatusBarNovo.class);

//				bind(Viewport.class).in(SINGLETON);

				addOptimizerIterationListener(Progress.class);
//				addOptimizerStateListener(ControlButtons.class);

//				addOptimizerIterationListener(ConvergencePlotData.class);

//				addToolBarService(ControlToolBarService.class);
//				addToolBarService(ViewsToolBarService.class);
		}
}

