package com.example.batch.Batch;

import com.example.batch.DTO.EmployeeDTO;
import com.example.batch.DTO.BatchTraitementDTO;
import com.example.batch.DTO.BatchErrorLogDTO;
import com.example.batch.Mapper.BatchTraitementMapper;
import com.example.batch.Repository.BatchTraitementRepository;
import com.example.batch.Repository.EmployeeRepository;
import com.example.batch.Service.BatchErrorLogService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.ItemReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class NDJsonItemReader implements ItemReader<EmployeeDTO> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BatchErrorLogService errorLogService;
    private final BatchTraitementRepository batchTraitementRepository;
    private final EmployeeRepository employeeRepository;
    private final List<File> ndjsonFiles;
    private Iterator<File> fileIterator;
    private BufferedReader currentReader;
    private String currentFileName = "";
    private int currentLineNumber = 0;

    // Per-file stats
    private int nbrLigne = 0;
    private int nbrLigneValide = 0;
    private int nbrLigneInvalide = 0;
    private LocalDateTime fileStartTime = null;
    private LocalDateTime fileStopTime = null;

    private String nextLine = null;
    private Set<String> emailsInCurrentFile = new HashSet<>(); // Track emails within a file
    private Set<String> immatriculationsInCurrentFile = new HashSet<>(); // Track immatriculations within a file

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern IMMAT_PATTERN = Pattern.compile("^RH\\d{4}$");

    public NDJsonItemReader(
            String directoryPath,
            BatchErrorLogService errorLogService,
            BatchTraitementRepository batchTraitementRepository,
            EmployeeRepository employeeRepository
    ) {
        this.errorLogService = errorLogService;
        this.batchTraitementRepository = batchTraitementRepository;
        this.employeeRepository = employeeRepository;
        try {
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                throw new RuntimeException("Le dossier NDJSON n'existe pas : " + directoryPath);
            }
            if (!directory.isDirectory()) {
                throw new RuntimeException("Chemin invalide, ce n'est pas un dossier : " + directoryPath);
            }
            File[] filesArr = directory.listFiles((dir, name) -> name.startsWith("os_") && name.endsWith(".ndjson"));
            ndjsonFiles = filesArr != null ? List.of(filesArr) : List.of();
            fileIterator = ndjsonFiles.iterator();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la lecture des fichiers NDJSON", e);
        }
    }

    @Override
    public EmployeeDTO read() {
        try {
            while (true) {
                if (currentReader == null) {
                    if (!fileIterator.hasNext()) {
                        return null;
                    }
                    File nextFile = fileIterator.next();
                    currentFileName = nextFile.getName();
                    currentReader = new BufferedReader(new FileReader(nextFile));
                    currentLineNumber = 1;

                    // Reset per-file stats
                    nbrLigne = 0;
                    nbrLigneValide = 0;
                    nbrLigneInvalide = 0;
                    fileStartTime = LocalDateTime.now();
                    emailsInCurrentFile.clear();
                    immatriculationsInCurrentFile.clear();
                }

                String line = (nextLine != null) ? nextLine : currentReader.readLine();
                nextLine = null;

                if (line == null) {
                    // End of file: Save BatchTraitementDTO, even if all lines were invalid!
                    fileStopTime = LocalDateTime.now();

                    BatchTraitementDTO traitementDTO = new BatchTraitementDTO(
                            null,
                            currentFileName,
                            nbrLigne,
                            nbrLigneValide,
                            nbrLigneInvalide,
                            fileStartTime,
                            fileStopTime
                    );
                    batchTraitementRepository.save(BatchTraitementMapper.toEntity(traitementDTO));

                    currentReader.close();
                    currentReader = null;
                    continue; // go to next file
                }

                nbrLigne++;

                if (line.trim().isEmpty() || line.trim().equals("{}")) {
                    nbrLigneInvalide++;
                    BatchErrorLogDTO errorLogDTO = new BatchErrorLogDTO(
                            null,
                            currentLineNumber,
                            "La ligne ne contient aucun employé.",
                            currentFileName
                    );
                    errorLogService.logError(errorLogDTO);
                    System.err.println(errorLogDTO.getErrorMessage());
                    currentLineNumber++;
                    continue;
                }

                try {
                    EmployeeDTO dto = objectMapper.readValue(line, EmployeeDTO.class);

                    // First name empty check
                    if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
                        nbrLigneInvalide++;
                        BatchErrorLogDTO errorLogDTO = new BatchErrorLogDTO(
                                null,
                                currentLineNumber,
                                "le prenom est vide",
                                currentFileName
                        );
                        errorLogService.logError(errorLogDTO);
                        System.err.println(errorLogDTO.getErrorMessage());
                        currentLineNumber++;
                        continue;
                    }

                    // Last name empty check
                    if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
                        nbrLigneInvalide++;
                        BatchErrorLogDTO errorLogDTO = new BatchErrorLogDTO(
                                null,
                                currentLineNumber,
                                "le nom est vide",
                                currentFileName
                        );
                        errorLogService.logError(errorLogDTO);
                        System.err.println(errorLogDTO.getErrorMessage());
                        currentLineNumber++;
                        continue;
                    }

                    // Email format validation
                    if (dto.getEmail() == null || !EMAIL_PATTERN.matcher(dto.getEmail()).matches()) {
                        nbrLigneInvalide++;
                        BatchErrorLogDTO errorLogDTO = new BatchErrorLogDTO(
                                null,
                                currentLineNumber,
                                "l'email est invalid : " + (dto.getEmail() != null ? dto.getEmail() : "null"),
                                currentFileName
                        );
                        errorLogService.logError(errorLogDTO);
                        System.err.println(errorLogDTO.getErrorMessage());
                        currentLineNumber++;
                        continue;
                    }

                    // Immatriculation format validation
                    if (dto.getImmatriculation() == null || !IMMAT_PATTERN.matcher(dto.getImmatriculation()).matches()) {
                        nbrLigneInvalide++;
                        BatchErrorLogDTO errorLogDTO = new BatchErrorLogDTO(
                                null,
                                currentLineNumber,
                                "immatriculation invalide : " + (dto.getImmatriculation() != null ? dto.getImmatriculation() : "null"),
                                currentFileName
                        );
                        errorLogService.logError(errorLogDTO);
                        System.err.println(errorLogDTO.getErrorMessage());
                        currentLineNumber++;
                        continue;
                    }

                    // Email duplicate in file validation
                    if (emailsInCurrentFile.contains(dto.getEmail())) {
                        nbrLigneInvalide++;
                        BatchErrorLogDTO errorLogDTO = new BatchErrorLogDTO(
                                null,
                                currentLineNumber,
                                "email en double dans le fichier : " + dto.getEmail(),
                                currentFileName
                        );
                        errorLogService.logError(errorLogDTO);
                        System.err.println(errorLogDTO.getErrorMessage());
                        currentLineNumber++;
                        continue;
                    }

                    // Immatriculation duplicate in file validation
                    if (immatriculationsInCurrentFile.contains(dto.getImmatriculation())) {
                        nbrLigneInvalide++;
                        BatchErrorLogDTO errorLogDTO = new BatchErrorLogDTO(
                                null,
                                currentLineNumber,
                                "immatriculation en double dans le fichier : " + dto.getImmatriculation(),
                                currentFileName
                        );
                        errorLogService.logError(errorLogDTO);
                        System.err.println(errorLogDTO.getErrorMessage());
                        currentLineNumber++;
                        continue;
                    }

                    // Email uniqueness in database
                    if (employeeRepository.existsByEmail(dto.getEmail())) {
                        nbrLigneInvalide++;
                        BatchErrorLogDTO errorLogDTO = new BatchErrorLogDTO(
                                null,
                                currentLineNumber,
                                "email deja utilise",
                                currentFileName
                        );
                        errorLogService.logError(errorLogDTO);
                        System.err.println(errorLogDTO.getErrorMessage());
                        currentLineNumber++;
                        continue;
                    }

                    // Immatriculation uniqueness in database
                    if (employeeRepository.existsByImmatriculation(dto.getImmatriculation())) {
                        nbrLigneInvalide++;
                        BatchErrorLogDTO errorLogDTO = new BatchErrorLogDTO(
                                null,
                                currentLineNumber,
                                "immatriculation deja utilisee",
                                currentFileName
                        );
                        errorLogService.logError(errorLogDTO);
                        System.err.println(errorLogDTO.getErrorMessage());
                        currentLineNumber++;
                        continue;
                    }

                    // Mark email and immatriculation as seen in this file
                    emailsInCurrentFile.add(dto.getEmail());
                    immatriculationsInCurrentFile.add(dto.getImmatriculation());

                    nbrLigneValide++;
                    currentLineNumber++;
                    return dto;
                } catch (Exception e) {
                    nbrLigneInvalide++;
                    BatchErrorLogDTO errorLogDTO = new BatchErrorLogDTO(
                            null,
                            currentLineNumber,
                            buildCustomErrorMessage(e, line, currentLineNumber),
                            currentFileName
                    );
                    errorLogService.logError(errorLogDTO);
                    System.err.println(errorLogDTO.getErrorMessage());
                    currentLineNumber++;
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Erreur lors de la lecture du fichier NDJSON", ex);
        }
    }

    private String buildCustomErrorMessage(Exception e, String line, int lineNumber) {
        if (e instanceof JsonMappingException mappingException) {
            if (!mappingException.getPath().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Champ invalide : ");
                for (JsonMappingException.Reference ref : mappingException.getPath()) {
                    sb.append(ref.getFieldName()).append(".");
                }
                sb.deleteCharAt(sb.length() - 1); // Remove last "."
                sb.append(" - ").append(mappingException.getOriginalMessage());
                if (!line.trim().isEmpty()) {
                    sb.append(" (Ligne : ").append(line).append(")");
                }
                return sb.toString();
            } else {
                String msg = "Champ invalide - " + mappingException.getOriginalMessage();
                if (!line.trim().isEmpty()) {
                    msg += " (Ligne : " + line + ")";
                }
                return msg;
            }
        } else if (e instanceof JsonParseException) {
            String msg = "JSON invalide.";
            if (!line.trim().isEmpty()) {
                msg += " (Ligne : " + line + ")";
            }
            return msg;
        } else {
            String msg = "Erreur inconnue.";
            if (!line.trim().isEmpty()) {
                msg += " (Ligne : " + line + ")";
            }
            return msg;
        }
    }

    public List<File> getNdjsonFiles() {
        return ndjsonFiles;
    }
}