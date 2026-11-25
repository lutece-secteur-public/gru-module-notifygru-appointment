/*
 * Copyright (c) 2002-2026, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.notifygru.modules.appointment.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.genericattributes.business.Entry;

/**
 * Tests the HTML values of the response and recap markers of AppointmentProvider, declared rich for notifygru.
 */
public class AppointmentProviderTest
{
    /**
     * A response of the user is written as text: its markup is escaped.
     */
    @Test
    public void testResponseIsEscaped( )
    {
        assertEquals( "&lt;b&gt;Tom &amp; Jerry&lt;/b&gt; &quot;x&quot;", AppointmentProvider.responseToHtml( "<b>Tom & Jerry</b> \"x\"", false ) );
    }

    /**
     * A rich text response is kept as the HTML entered: notifygru sanitizes it when it renders the message.
     */
    @Test
    public void testRichTextResponseIsKept( )
    {
        assertEquals( "<p>Hello <strong>world</strong></p>", AppointmentProvider.responseToHtml( "<p>Hello <strong>world</strong></p>", true ) );
    }

    /**
     * The entry of a response is loaded without its position (always 0): the marker of the response is the one described for the entry, built on the
     * position of the entry in the form, not reponse_0 for every response.
     */
    @Test
    public void testResponseMarkerUsesThePositionOfTheEntryInTheForm( )
    {
        Entry entry = new Entry( );
        entry.setIdEntry( 3431 );
        assertEquals( "reponse_2", AppointmentProvider.getEntryMarker( entry, Map.of( 3430, 1, 3431, 2 ) ) );
    }

    /**
     * The slots of a multi-slot appointment are read in the order of their ids: the recap starts with the earliest slot and
     * ends with the latest one.
     */
    @Test
    public void testSlotsOfTheRecapAreSortedByStart( )
    {
        Slot later = slot( 10, 30 );
        Slot earlier = slot( 10, 0 );

        List<Slot> sorted = AppointmentProvider.sortedByStart( List.of( later, earlier ) );

        assertEquals( earlier, sorted.get( 0 ) );
        assertEquals( later, sorted.get( 1 ) );
    }

    private static Slot slot( int nHour, int nMinute )
    {
        Slot slot = new Slot( );
        slot.setStartingDateTime( LocalDateTime.of( 2026, 11, 27, nHour, nMinute ) );
        slot.setEndingDateTime( slot.getStartingDateTime( ).plusMinutes( 30 ) );
        return slot;
    }
}
