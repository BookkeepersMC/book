package book.mappings.tasks.mappings;

import book.mappings.util.Password;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
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
 *     <li> {@value book.mappings.BookMappingsPlugin#ENIGMA_SERVER_PORT_PROP} then
 *     		{@value book.mappings.BookMappingsPlugin#PORT_PROP} for {@link #getPort() port}
 *     <li> {@value book.mappings.BookMappingsPlugin#ENIGMA_SERVER_PASSWORD_PROP} then
 *     		{@value book.mappings.BookMappingsPlugin#PASSWORD_PROP} for {@link #getPassword() password}
 *     <li> {@value book.mappings.BookMappingsPlugin#ENIGMA_SERVER_LOG_PROP} then
 *     		{@value book.mappings.BookMappingsPlugin#LOG_PROP} for the path to {@link #getLogFile() logFile}
 *     <li> {@value book.mappings.BookMappingsPlugin#ENIGMA_SERVER_ARGS_PROP} then
 *     		{@value book.mappings.BookMappingsPlugin#ARGS_PROP} for any additional command line args
 * </ul>
 */
public abstract class EnigmaMappingsServerTask extends AbstractEnigmaMappingsTask {
    @Optional
    @Input
    public abstract Property<String> getPort();

    @Optional
    @Input
    public abstract Property<Password> getPassword();

    @Optional
    @OutputFile
    public abstract RegularFileProperty getLogFile();

    public EnigmaMappingsServerTask() {
        // this configuration can stay here because it's what make this an EnigmaMappingsServerTask
        this.getMainClass().convention(DedicatedEnigmaServer.class.getName());
    }

    @Override
    public void exec() {
        final List<String> optionalArgs = new ArrayList<>();

        toOptional(this.getPort()).ifPresent(port -> {
            optionalArgs.add("-port");
            optionalArgs.add(port);
        });

        toOptional(this.getPassword().map(Password::password)).ifPresent(password -> {
            optionalArgs.add("-password");
            optionalArgs.add(password);
        });

        toOptional(this.getLogFile().getAsFile()).ifPresent(log -> {
            optionalArgs.add("-log");
            optionalArgs.add(log.getAbsolutePath());
        });

        this.args(optionalArgs);

        super.exec();
    }
}
