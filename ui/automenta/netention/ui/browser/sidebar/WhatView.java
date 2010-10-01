/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.netention.ui.browser.sidebar;

import automenta.netention.ui.browser.Sidebar.SidebarView;
import com.vaadin.ui.AbstractLayout;

/**
 *
 * @author seh
 */
public class WhatView extends SidebarView {

    public WhatView() {
        super("What");
    }

    @Override
    public void render(AbstractLayout target) {
//        User u = SessionHandler.get();
//        if (u == null) {
//            //target.addComponent(new Label("Login to see personal details."));
//            u = NApplication.getAnonymousSelf();
//        }
//
//        Self s = (Self)u;
//        Iterator<Node> di = s.iterateDetails();
//        while (di.hasNext()) {
//            Node n = di.next();
//            target.addComponent(new WideDetailIcon(n));
//        }
        
    }

//    @Override
//    public void render(AbstractLayout target) {
//        final Object[][] planets = new Object[][]{
//            new Object[]{"Mercury"},
//            new Object[]{"Venus"},
//            new Object[]{"Earth", "The Moon"},
//            new Object[]{"Mars", "Phobos", "Deimos"},
//            new Object[]{"Jupiter", "Io", "Europa", "Ganymedes",
//                "Callisto"},
//            new Object[]{"Saturn", "Titan", "Tethys", "Dione",
//                "Rhea", "Iapetus"},
//            new Object[]{"Uranus", "Miranda", "Ariel", "Umbriel",
//                "Titania", "Oberon"},
//            new Object[]{"Neptune", "Triton", "Proteus", "Nereid",
//                "Larissa"}};
//
//        Tree tree = new Tree("The Planets and Major Moons");
//        /* Add planets as root items in the tree. */
//        for (int i = 0; i < planets.length; i++) {
//            String planet = (String) (planets[i][0]);
//            tree.addItem(planet);
//
//            if (planets[i].length == 1) {
//                // The planet has no moons so make it a leaf.
//                tree.setChildrenAllowed(planet, false);
//            } else {
//                // Add children (moons) under the planets.
//                for (int j = 1; j < planets[i].length; j++) {
//                    String moon = (String) planets[i][j];
//
//                    // Add the item as a regular item.
//                    tree.addItem(moon);
//
//                    // Set it to be a child.
//                    tree.setParent(moon, planet);
//
//                    // Make the moons look like leaves.
//                    tree.setChildrenAllowed(moon, false);
//                }
//                // Expand the subtree.
//                tree.expandItemsRecursively(planet);
//            }
//        }
//        target.addComponent(tree);
//    }
    
}
