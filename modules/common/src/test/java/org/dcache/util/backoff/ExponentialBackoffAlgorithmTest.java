/*
COPYRIGHT STATUS:
Dec 1st 2001, Fermi National Accelerator Laboratory (FNAL) documents and
software are sponsored by the U.S. Department of Energy under Contract No.
DE-AC02-76CH03000. Therefore, the U.S. Government retains a  world-wide
non-exclusive, royalty-free license to publish or reproduce these documents
and software for U.S. Government purposes.  All documents and software
available from this server are protected under the U.S. and Foreign
Copyright Laws, and FNAL reserves all rights.

Distribution of the software available from this server is free of
charge subject to the user following the terms of the Fermitools
Software Legal Information.

Redistribution and/or modification of the software shall be accompanied
by the Fermitools Software Legal Information  (including the copyright
notice).

The user is asked to feed back problems, benefits, and/or suggestions
about the software to the Fermilab Software Providers.

Neither the name of Fermilab, the  URA, nor the names of the contributors
may be used to endorse or promote products derived from this software
without specific prior written permission.

DISCLAIMER OF LIABILITY (BSD):

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED  WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED  WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL FERMILAB,
OR THE URA, OR THE U.S. DEPARTMENT of ENERGY, OR CONTRIBUTORS BE LIABLE
FOR  ANY  DIRECT, INDIRECT,  INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
OF SUBSTITUTE  GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY  OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE  POSSIBILITY OF SUCH DAMAGE.

Liabilities of the Government:

This software is provided by URA, independent from its Prime Contract
with the U.S. Department of Energy. URA is acting independently from
the Government and in its own private capacity and is not acting on
behalf of the U.S. Government, nor as its contractor nor its agent.
Correspondingly, it is understood and agreed that the U.S. Government
has no connection to this software and in no manner whatsoever shall
be liable for nor assume any responsibility or obligation for any claim,
cost, or damages arising out of or resulting from the use of the software
available from this server.

Export Control:

All documents and software available from this server are subject to U.S.
export control laws.  Anyone downloading information from this server is
obligated to secure any necessary Government licenses before exporting
documents or software obtained from this server.
 */
package org.dcache.util.backoff;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Simple consistency tests for the {@link ExponentialBackoffAlgorithm} class.
 *
 * @author arossi
 */
public class ExponentialBackoffAlgorithmTest {

    private long finalDelay;
    private long maxDelay;
    private ExponentialBackoffAlgorithmFactory factory
        = new ExponentialBackoffAlgorithmFactory();
    private IBackoffAlgorithm algorithm;

    @Test
    public void shouldNotAcceptIncorrectValuesForDelay() {
        givenMinValueOf(2);
        givenMaxValueOf(1);
        assertThat(backoffSuccessfullyInitialized(), is(false));
    }

    @Test
    public void shouldNotWaitWhenMaxAllowedAttemptsReached() {
        givenUnit(TimeUnit.SECONDS);
        givenMinValueOf(1);
        givenMaxValueOf(5);
        givenQuitAtMaxDelay(false);
        givenMaxAllowedAttempts(9);
        afterAttempts(9);
        assertThat(finalDelay == IBackoffAlgorithm.NO_WAIT, is(true));
    }

    @Test
    public void shouldNotWaitWhenMaxDelayReachedAndQuitIsTrue() {
        givenUnit(TimeUnit.SECONDS);
        givenMinValueOf(1);
        givenMaxValueOf(5);
        givenQuitAtMaxDelay(true);
        afterAttempts(9);
        assertThat(finalDelay == IBackoffAlgorithm.NO_WAIT, is(true));
    }

    @Test
    public void shouldReturnLessThanMaxValueAfterEightAttempts() {
        givenUnit(TimeUnit.SECONDS);
        givenMinValueOf(1);
        givenMaxValueOf(5);
        afterAttempts(8);
        assertThat(TimeUnit.MILLISECONDS.toSeconds(finalDelay) < maxDelay,
                        is(true));
    }

    @Test
    public void shouldReturnMaxValueAfterNineAttempts() {
        givenUnit(TimeUnit.SECONDS);
        givenMinValueOf(1);
        givenMaxValueOf(5);
        afterAttempts(9);
        assertThat(TimeUnit.MILLISECONDS.toSeconds(finalDelay) == maxDelay,
                        is(true));
    }

    @Test
    public void shouldWaitWhenMaxDelayReachedAndQuitIsFalse() {
        givenUnit(TimeUnit.SECONDS);
        givenMinValueOf(1);
        givenMaxValueOf(2);
        givenQuitAtMaxDelay(false);
        afterAttempts(3);
        assertThat(finalDelay == IBackoffAlgorithm.NO_WAIT, is(false));
    }

    private void afterAttempts(int attempts) {
        algorithm = factory.getAlgorithm();
        finalDelay = 0;
        for (int t = 0; t < attempts; t++) {
            finalDelay = algorithm.getWaitDuration();
        }
    }

    private boolean backoffSuccessfullyInitialized() {
        try {
            algorithm = factory.getAlgorithm();
            return true;
        } catch (final IllegalArgumentException t) {
            return false;
        }
    }

    private void givenMaxAllowedAttempts(int i) {
        factory.setMaxAllowedAttempts(i);
    }

    private void givenMaxValueOf(long maxDelay) {
        factory.setMaxDelay(maxDelay);
        this.maxDelay = maxDelay;
    }

    private void givenMinValueOf(long minDelay) {
        factory.setMinDelay(minDelay);
    }

    private void givenQuitAtMaxDelay(boolean quit) {
        factory.setQuitAtMaxDelay(quit);
    }

    private void givenUnit(TimeUnit unit) {
        factory.setMinUnit(unit);
        factory.setMaxUnit(unit);
    }
}
