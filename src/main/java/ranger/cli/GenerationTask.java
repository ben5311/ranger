package ranger.cli;

import me.tongfei.progressbar.ProgressBar;
import ranger.ObjectGenerator;
import ranger.cli.writer.OutputWriter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GenerationTask extends Thread {
    private final ObjectGenerator<?> generator;
    private final List<OutputWriter> writers;
    private final List<Long> counts;
    private final ProgressBar progressBar;

    public GenerationTask(ObjectGenerator<?> generator, List<OutputWriter> writers, List<Long> counts,
                          ProgressBar progressBar) {
        if (writers.size() != counts.size()) {
            throw new IllegalArgumentException("writers and counts must be equally sized");
        }
        this.generator = generator;
        this.writers = writers;
        this.counts = counts;
        this.progressBar = progressBar;
    }

    @Override
    public void run() {
        try {
            generateObjects();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateObjects() throws IOException {
        long remaining = Collections.max(counts);
        while (remaining > 0) {
            Object generatedObject = generator.next();
            if (writers.size() == 1) {    // process single output
                writers.get(0).writeObject(generatedObject);
            } else {   // process multiple outputs
                for (int i = 0; i < writers.size(); i++) {
                    long count = counts.get(i);
                    if (count > 0) {
                        OutputWriter writer = writers.get(i);
                        writer.writeObject(((List<?>) generatedObject).get(i));
                        counts.set(i, count - 1);
                        writer.writeObject(generatedObject);
                    }
                }
            }
            if (progressBar != null) { progressBar.step(); }
            remaining--;
        }
    }

}
