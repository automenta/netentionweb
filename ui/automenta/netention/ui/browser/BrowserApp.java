/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.netention.ui.browser;

import automenta.netention.ui.NApplication;
import automenta.netention.ui.game.GameWindow;

/**
 * demo netention application
 */
public class BrowserApp extends NApplication {

    @Override
    public void init() {
        super.init();

        setTheme("netention-gray");

        AppWindow bw = new GameWindow(this);
        setMainWindow(bw);

        //bw.newDetail();
    }

}
