package giusa.software.tools.eclipse.openosfolder;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class EclipseExtensionsPlugin extends AbstractUIPlugin {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "com.software.gag.eclipse.extensions"; //$NON-NLS-1$

	/** singleton instance */
	private static EclipseExtensionsPlugin plugin;

	/**
	 * The constructor
	 */
	public EclipseExtensionsPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static EclipseExtensionsPlugin getDefault() {
		return plugin;
	}
}
