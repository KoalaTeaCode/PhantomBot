/*
 * Copyright (C) 2017 phantombot.tv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.mast3rplan.phantombot.event;

import java.util.concurrent.Executors;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.IBusConfiguration;

public class EventBus {
    private static final EventBus instance = new EventBus();
    private final MBassador bus;
    private boolean killed = false;

    /*
     * Constructor for this class
     */
    private EventBus() {
        // Create bus.
        this.bus = new MBassador(new BusConfiguration()
            .addFeature(Feature.SyncPubSub.Default())
            .addFeature(Feature.AsynchronousHandlerInvocation.Default())
            .addFeature(Feature.AsynchronousMessageDispatch.Default())
            .addPublicationErrorHandler(new ExceptionHandler())
            .setProperty(IBusConfiguration.Properties.BusId, "me.mast3rplan.phantombot.event.EventBus::bus"));
    }

    /*
     * @function instance
     *
     * @return Object
     */
    public static EventBus instance() {
        return instance;
    }

    /*
     * @function register
     *
     * @param {Listener} listener
     */
    public void register(Listener listener) {
        this.bus.subscribe(listener);
    }

    /*
     * @function unregister
     *
     * @param {Listener} listener
     */
    public void unregister(Listener listener) {
        this.bus.unsubscribe(listener);
    }

    /*
     * @function postAsync
     *
     * @param {Event} event
     */
    @SuppressWarnings("unchecked")
    public void postAsync(Event event) {
        if (!killed) {
            this.bus.publishAsync(event);
        }
    }

    /*
     * @function post
     *
     * @param {Event} event
     */
    @SuppressWarnings("unchecked")
    public void post(Event event) {
        if (!killed) {
            this.bus.publish(event);
        }
    }

    /*
     * @function kill
     */
    public void kill() {
        this.killed = true;
    }
}

/*
 * This goes in the ScriptEventManager class.
 *
 *   @Handler
 *   public void synchronousHandler(Event event) {
 *       try {
 *           for (EventHandlerEntry entry : entries) {
 *               if (event.getClass().isAssignableFrom(entry.eventClass)) {                
 *                   entry.handler.handle(event);
 *                   com.gmt2001.Console.debug.println("Dispatching event " + entry.eventClass.getName());
 *               }
 *           }
 *       } catch (Exception e) {
 *           com.gmt2001.Console.err.println("Failed to dispatch event " + event.getClass().getName());
 *           com.gmt2001.Console.err.printStackTrace(e);
 *       }
 *  }
 */

