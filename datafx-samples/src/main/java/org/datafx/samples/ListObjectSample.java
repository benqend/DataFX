/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datafx.samples;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ListProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.datafx.provider.ListObjectDataProvider;
import org.datafx.reader.FileSource;
import org.datafx.reader.util.XmlConverter;

/**
 *
 * @author johan
 */
public class ListObjectSample {
	
	public ListObjectSample() {
	}

	public Node getContent(Scene scene) {
		// TabPane
		final TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		tabPane.setPrefWidth(scene.getWidth());
		tabPane.setPrefHeight(scene.getHeight());

		tabPane.prefWidthProperty().bind(scene.widthProperty());
		tabPane.prefHeightProperty().bind(scene.heightProperty());

		Tab localTab = new Tab("local");
		buildLocalTab(localTab);
		tabPane.getTabs().add(localTab);

		Tab networkTab = new Tab("network");
		buildNetworkTab(networkTab);
		tabPane.getTabs().add(networkTab);

		return tabPane;
	}

	private void buildLocalTab(Tab tab) {
		try {
			URL resource = this.getClass().getResource("manybooks.xml");
            XmlConverter converter = new XmlConverter("book", Book.class);
			FileSource<Book> dr = new FileSource(new File(resource.getFile()),converter, Book.class);
			ListObjectDataProvider<Book> sodp = new ListObjectDataProvider(dr);
			sodp.retrieve();
		
			final ListProperty<Book> op = sodp.getData();
            ListView lv = new ListView(op.get());
			tab.setContent(lv);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(SingleObjectSample.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void buildNetworkTab(Tab tab) {
	}
}
