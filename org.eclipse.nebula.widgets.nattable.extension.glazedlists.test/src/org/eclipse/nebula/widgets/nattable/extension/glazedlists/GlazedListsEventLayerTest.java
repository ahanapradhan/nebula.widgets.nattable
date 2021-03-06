/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.extension.glazedlists;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.command.DisposeResourcesCommand;
import org.eclipse.nebula.widgets.nattable.dataset.fixture.data.BlinkingRowDataFixture;
import org.eclipse.nebula.widgets.nattable.dataset.fixture.data.RowDataFixture;
import org.eclipse.nebula.widgets.nattable.dataset.fixture.data.RowDataListFixture;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.fixture.DataLayerFixture;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.fixture.LayerListenerFixture;
import org.eclipse.nebula.widgets.nattable.layer.event.PropertyUpdateEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.RowStructuralRefreshEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

public class GlazedListsEventLayerTest {

    private EventList<RowDataFixture> listFixture;
    private GlazedListsEventLayer<RowDataFixture> layerUnderTest;
    private LayerListenerFixture listenerFixture;

    @Before
    public void setup() {
        this.listFixture = GlazedLists.eventList(RowDataListFixture.getList());

        this.layerUnderTest = new GlazedListsEventLayer<RowDataFixture>(
                new DataLayerFixture(), this.listFixture);
        this.layerUnderTest.setTestMode(true);

        this.listenerFixture = new LayerListenerFixture();
        this.layerUnderTest.addLayerListener(this.listenerFixture);
    }

    @Ignore
    // This is failing in hudson, but works fine locally. Ignoring for now.
    @Test
    public void shouldConflateEvents() throws Exception {
        this.listFixture.add(RowDataFixture.getInstance("T1", "A"));
        Thread.sleep(100);

        this.listFixture.add(RowDataFixture.getInstance("T2", "A"));
        Thread.sleep(100);

        Assert.assertNotNull(this.listenerFixture
                .getReceivedEvent(RowStructuralRefreshEvent.class));
    }

    @Test
    public void shouldShutConflaterThreadDownWhenNatTableIsDisposed()
            throws Exception {
        Assert.assertFalse(this.layerUnderTest.isDisposed());

        this.listFixture.add(RowDataFixture.getInstance("T1", "A"));
        Thread.sleep(100);

        this.listFixture.add(RowDataFixture.getInstance("T2", "A"));
        Thread.sleep(100);

        this.layerUnderTest.doCommand(new DisposeResourcesCommand());
        Assert.assertTrue(this.layerUnderTest.isDisposed());
    }

    @Test
    public void propertyChangeEventshouldBePropagatedImmediately()
            throws Exception {
        List<BlinkingRowDataFixture> list = BlinkingRowDataFixture
                .getList(this.layerUnderTest);
        list.get(0).setAsk_price(100.0F);

        Assert.assertNotNull(this.listenerFixture
                .getReceivedEvent(PropertyUpdateEvent.class));
    }
}
