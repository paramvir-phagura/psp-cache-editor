package com.runescape.loaders.interfaces;

import java.awt.Component;
import java.awt.Image;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.ReplicateScaleFilter;
import java.io.IOException;

import javax.swing.JComponent;

import com.runescape.io.InputStream;
import com.runescape.store.Store;
import com.runescape.util.Utils;

public class Interface {

	public int id;
	public Store cache;
	public IComponent[] components;
	public JComponent[] jcomponents;


	public static void main(String[] args) throws IOException {
		Store rscache = new Store("cache/");
		for(int i = 0; i < Utils.getInterfaceDefinitionsSize(rscache); i++) {
			try {
				new Interface(i, rscache);
			}catch(Throwable e) {

			}
		}
	}

	public Interface(int id, Store cache) {
		this(id,cache,true);
	}
	public Interface(int id, Store cache, boolean load) {
		this.id = id;
		this.cache = cache;
		if(load)
			getComponents();
	}

	public void draw(JComponent parent) {

	}

	public Image resizeImage(Image image, int width, int height, Component c) {
	      ImageFilter replicate = new ReplicateScaleFilter(width, height);
	      ImageProducer prod = new FilteredImageSource(image.getSource(),replicate);
	      return c.createImage(prod);
	}


	public void getComponents() {
		if(Utils.getInterfaceDefinitionsSize(cache) <= id)
			throw new RuntimeException("Invalid interface id.");
		components = new IComponent[Utils.getInterfaceDefinitionsComponentsSize(cache, id)];
		for(int componentId = 0; componentId < components.length; componentId++) {
			components[componentId] = new IComponent();
			components[componentId].hash = id << 16 | componentId;
			byte[] data = cache.getIndexes()[3].getFile(id, componentId);
			if (data == null)
				throw new RuntimeException("Interface "+id+", component "+componentId+" data is null.");
                        System.out.println("Interface: "+id+" - ComponentId: "+componentId);
			if (data[0] != -1)
				components[componentId].decodeNoscriptsFormat(new InputStream(data));
			else
				components[componentId].decodeScriptsFormat(new InputStream(data));
		}
	}
}
