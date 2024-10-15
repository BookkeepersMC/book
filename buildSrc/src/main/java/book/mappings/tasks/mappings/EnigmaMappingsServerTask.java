package book.mappings.tasks.mappings;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.options.Option;
import org.quiltmc.enigma.network.DedicatedEnigmaServer;

import java.util.ArrayList;
import java.util.List;

import static book.mappings.util.ProviderUtil.toOptional;

/**
 * Starts a {@link DedicatedEnigmaServer}.
 * <p>
 * Optional inputs will be passed as command line args if present.
 * <p>
 * If {@link book.mappings.BookMappingsPlugin QuiltMappingsPlugin} is applied
 * the follow gradle properties will be searched for default values:
 * <ul>
 *     <li> {@value book.mappings.BookMappingsPlugin#ENIGMA_SERVER_PORT_PROP}
 *     for the {@link #getPort() port}
 *     <li> {@value book.mappings.BookMappingsPlugin#ENIGMA_SERVER_PASSWORD_PROP}
 *     for the {@link #getPassword() password}
 *     <li> {@value book.mappings.BookMappingsPlugin#ENIGMA_SERVER_LOG_PROP}
 *     for the {@link #getLog() log} path
 *     <li> {@value book.mappings.BookMappingsPlugin#ENIGMA_SERVER_ARGS_PROP}
 *     for any additional command line args
 * </ul>
 */
public abstract class EnigmaMappingsServerTask extends AbstractEnigmaMappingsTask {
    public static final String PORT_OPTION = "port";
    public static final String PASSWORD_OPTION = "password";
    public static final String LOG_OPTION = "log";

    @Optional
    @Option(option = PORT_OPTION, description = "The enigma port that the server will run on.")
    @Input
    public abstract Property<String> getPort();

    @Optional
    @Option(option = PASSWORD_OPTION, description = "The enigma server password.")
    @Input
    public abstract Property<String> getPassword();

    @Optional
    @Option(option = LOG_OPTION, description = "The path that the enigma server will write logs to.")
    @OutputFile
    public abstract RegularFileProperty getLog();

    public EnigmaMappingsServerTask() {
        // this configuration can stay here because it's what makes this an EnigmaMappingsServerTask
        this.getMainClass().convention(DedicatedEnigmaServer.class.getName());
        this.getMainClass().finalizeValue();
    }

    @Override
    public void exec() {
        final List<String> optionalArgs = new ArrayList<>();

        toOptional(this.getPort()).ifPresent(port -> {
            optionalArgs.add("-" + PORT_OPTION);
            optionalArgs.add(port);
        });

        toOptional(this.getPassword()).ifPresent(password -> {
            optionalArgs.add("-" + PASSWORD_OPTION);
            optionalArgs.add(password);
        });

        toOptional(this.getLog().getAsFile()).ifPresent(log -> {
            optionalArgs.add("-" + LOG_OPTION);
            optionalArgs.add(log.getAbsolutePath());
        });

        this.args(optionalArgs);

        super.exec();
    }
}
