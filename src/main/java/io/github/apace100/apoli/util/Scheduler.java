package io.github.apace100.apoli.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

/**
 * This is free and unencumbered software released into the public domain.
 * <p>
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * <p>
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * <p>
 * For more information, please refer to <http://unlicense.org/>
 */
public class Scheduler {
	private final Int2ObjectMap<List<Consumer<MinecraftServer>>> taskQueue = new Int2ObjectOpenHashMap<>();
	private int currentTick = 0;

	public Scheduler() {
		MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent event) -> {
			MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
			if (event.phase == TickEvent.Phase.END) {
				this.currentTick = server.getTickCount();
				List<Consumer<MinecraftServer>> runnables = this.taskQueue.remove(this.currentTick);
				if (runnables != null) {
					for (Consumer<MinecraftServer> runnable : runnables) {
						runnable.accept(server);
						if (runnable instanceof Repeating repeating) {// reschedule repeating tasks
							if (repeating.shouldQueue(this.currentTick))
								this.queue(runnable, repeating.next);
						}
					}
				}
			}
		});
	}

	/**
	 * queue a one time task to be executed on the server thread
	 *
	 * @param tick how many ticks in the future this should be called, where 0 means at the end of the current tick
	 * @param task the action to perform
	 */
	public void queue(Consumer<MinecraftServer> task, int tick) {
		this.taskQueue.computeIfAbsent(this.currentTick + tick + 1, t -> new ArrayList<>()).add(task);
	}

	/**
	 * schedule a repeating task that is executed infinitely every n ticks
	 *
	 * @param task     the action to perform
	 * @param tick     how many ticks in the future this event should first be called
	 * @param interval the number of ticks in between each execution
	 */
	public void repeating(Consumer<MinecraftServer> task, int tick, int interval) {
		this.repeatWhile(task, null, tick, interval);
	}

	/**
	 * repeat the given task until the predicate returns false
	 *
	 * @param task     the action to perform
	 * @param requeue  whether or not to reschedule the task again, with the parameter being the current tick
	 * @param tick     how many ticks in the future this event should first be called
	 * @param interval the number of ticks in between each execution
	 */
	public void repeatWhile(Consumer<MinecraftServer> task, IntPredicate requeue, int tick, int interval) {
		this.queue(new Repeating(task, requeue, interval), tick);
	}

	/**
	 * repeat the given task n times more than 1 time
	 *
	 * @param task  the action to perform
	 * @param times the number of <b>additional</b> times the task should be scheduled
	 * @param tick  how many ticks in the future this event should first be called
	 *              * @param interval the number of ticks in between each execution
	 */
	public void repeatN(Consumer<MinecraftServer> task, int times, int tick, int interval) {
		this.repeatWhile(task, new IntPredicate() {
			private int remaining = times;

			@Override
			public boolean test(int value) {
				return this.remaining-- > 0;
			}
		}, tick, interval);
	}

	private static final class Repeating implements Consumer<MinecraftServer> {
		public final int next;
		private final Consumer<MinecraftServer> task;
		private final IntPredicate requeue;

		private Repeating(Consumer<MinecraftServer> task, IntPredicate requeue, int interval) {
			this.task = task;
			this.requeue = requeue;
			this.next = interval;
		}

		public boolean shouldQueue(int predicate) {
			if (this.requeue == null)
				return true;
			return this.requeue.test(predicate);
		}


		@Override
		public void accept(MinecraftServer server) {
			this.task.accept(server);
		}
	}
}
