package com.github.numichi.processes;

import com.github.numichi.exceptions.DirectoryAccessException;
import com.github.numichi.services.PermissionScanner;
import lombok.Builder;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Service to perform a deep scan of a directory, collecting files with a specific extension
 * and handling permission issues based on the provided context.
 * <p>
 * Important: This class is not a Spring component because it is stateful and should be instantiated directly!
 */
public class DeepScanProcess {

    /**
     * Service for checking directory permissions.
     */
    private final PermissionScanner permissionScanner;

    /**
     * Initial context for the deep scan process, including folder path, file extension, and warning flag.
     */
    private final DeepScanContext context;

    /**
     * Set of unique file paths found during the scan.
     */
    private final Set<String> files = new HashSet<>();

    /**
     * Queue of directories to be scanned.
     */
    private final Queue<Path> queue = new LinkedList<>();

    /**
     * List of permission problems encountered during scanning.
     */
    private final List<String> permissionProblems = new ArrayList<>();

    /**
     * Constructs a DeepScanProcess with the specified permission scanner and context.
     *
     * @param permissionScanner the service for checking directory permissions
     * @param context           the initial context for the deep scan process
     */
    public DeepScanProcess(PermissionScanner permissionScanner, DeepScanContext context) {
        this.permissionScanner = permissionScanner;
        this.context = context;
    }

    /**
     * Runs the deep scan process, starting from the specified folder and looking for files with the given extension.
     *
     * @return a Result object containing the set of unique file paths found and any permission problems encountered
     * @throws DirectoryAccessException if a directory cannot be accessed and warn is false in the predefined context
     * @throws IllegalArgumentException if the provided folder path is not absolute or not a directory in the predefined context
     */
    public Result run() {
        Path rootPath = Paths.get(context.folder());

        permissionScanner.checkReadPermission(rootPath);

        queue.add(rootPath);

        processDirectoryQueue();

        return new Result(files, permissionProblems);
    }

    /**
     * Processes the directory queue, scanning each directory and processing its entries.
     */
    private void processDirectoryQueue() {
        while (!queue.isEmpty()) {
            scanDirectory(queue.poll());
        }
    }

    /**
     * Scans a directory, processing each entry (file or subdirectory).
     *
     * @param directory the directory to scan
     */
    private void scanDirectory(Path directory) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                processEntry(entry);
            }
        } catch (IOException e) {
            permissionProblems.add("Cannot access directory: " + directory.toAbsolutePath());
        }
    }

    /**
     * Process a file system entry. If it's a directory, try to add it to the queue.
     * If it's a file with the specified extension, add it to the files set.
     *
     * @param entry the file system entry to process
     * @throws DirectoryAccessException if permission is denied and warn is false in run context
     */
    private void processEntry(Path entry) throws DirectoryAccessException {
        if (Files.isDirectory(entry)) {
            processDirectory(entry);
        } else {
            if (hasExtension(entry, context.ext())) {
                files.add(entry.getFileName().toString());
            }
        }
    }

    /**
     * Process a directory by checking permissions and adding it to the queue.
     * If permission is denied and warn is true, log the problem instead of throwing an exception.
     *
     * @param directory the directory to process
     * @throws DirectoryAccessException if permission is denied and warn is false in run context
     */
    private void processDirectory(Path directory) throws DirectoryAccessException {
        try {
            permissionScanner.checkReadPermission(directory);
            queue.add(directory);
        } catch (DirectoryAccessException e) {
            if (!context.warn()) {
                throw e;
            }

            permissionProblems.add(e.getMessage());
        }
    }

    /**
     * Checks if the given path has the specified extension.
     *
     * @param path the path to check
     * @param ext the extension to match (with or without leading dot)
     * @return true if the path has the given extension, false otherwise
     */
    private boolean hasExtension(Path path, String ext) {
        String fileName = path.getFileName().toString();
        String normalizedExt = ext.startsWith(".") ? ext : "." + ext;
        return fileName.endsWith(normalizedExt);
    }

    @Builder
    public record Result(Set<String> fileNames, List<String> permissionProblems) {
        public List<String> orderedFileNames() {
            return fileNames.stream().sorted().toList();
        }
    }
}
