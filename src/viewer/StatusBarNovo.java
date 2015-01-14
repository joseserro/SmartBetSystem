package viewer;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;

import org.opt4j.core.config.visualization.DelayTask;
import org.opt4j.core.optimizer.Optimizer;
import org.opt4j.core.optimizer.OptimizerIterationListener;
import org.opt4j.core.optimizer.OptimizerStateListener;
import org.opt4j.core.start.Progress;

import com.google.inject.Inject;

/**
 * The {@link StatusBarNovo} contains informations about the optimization progress
 * and time per iteration.
 * 
 * @author lukasiewycz
 * 
 */
public class StatusBarNovo implements OptimizerIterationListener, OptimizerStateListener {

	protected final Progress progress;

	protected final DelayTask task = new DelayTask(40);

	protected final JLabel timeLabel = new JLabel();

	protected JProgressBar bar;

	protected final static JPanel panel = new JPanel();

	protected long time;

	/**
	 * Constructs a {@link StatusBarNovo}.
	 * 
	 * @param progress
	 *            the progress
	 */
	@Inject
	public StatusBarNovo(Progress progress) {
		this.progress = progress;
	}

	/**
	 * Initialization. This method has to called once after construction.
	 */

	public JPanel getPanel(){
		return panel;
	}

	public void init() {
		UIManager.put("ProgressBar.selectionBackground",Color.BLUE);
		UIManager.put("ProgressBar.selectionForeground",Color.WHITE);
		bar = new JProgressBar();
		panel.setLayout(new BorderLayout());
		JPanel container = new JPanel(new BorderLayout());
		bar.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		bar.setMinimum(0);
		bar.setMaximum(progress.getMaxIterations());
		bar.setUI(new BasicProgressBarUI());
		bar.setStringPainted(true);

		container.add(bar);
		panel.add(container, BorderLayout.CENTER);

		time = System.nanoTime();

		update("init");
	}

	protected void update(final String progressMessage) {
		task.execute(new Runnable() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						String pm=progressMessage;
						if(pm==null)
							pm = "" + progress.getCurrentIteration() + "/" + progress.getMaxIterations();
						bar.setValue(progress.getCurrentIteration());
						bar.setString(pm);
					}
				});
			}
		});
	}

	/**
	 * Returns the component.
	 * 
	 * @return the component
	 */
	public static JComponent get() {
		return panel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opt4j.core.optimizer.OptimizerIterationListener#iterationComplete
	 * (org.opt4j.core.optimizer.Optimizer, int)
	 */
	@Override
	public void iterationComplete(int iteration) {
			update(null);
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
		update("Iterações terminadas");
	}
}