package swp.com.zeitenrechner;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class ZeitenRechnerController {
    // Time calculator fields
    @FXML
    private TextField hours1Field;
    @FXML
    private TextField minutes1Field;
    @FXML
    private TextField seconds1Field;
    @FXML
    private TextField hours2Field;
    @FXML
    private TextField minutes2Field;
    @FXML
    private TextField seconds2Field;
    @FXML
    private TextField resultField;

    // Timer fields
    @FXML
    private TextField timerField;
    @FXML
    private Label timerLabel;

    // Converter fields
    @FXML
    private TextField convertInput;
    @FXML
    private ComboBox<String> fromUnitCombo;
    @FXML
    private ComboBox<String> toUnitCombo;
    @FXML
    private Label convertResult;

    // Date to day-of-week fields
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label dayOfWeekLabel;

    // Age calculator fields
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private Label ageLabel;
    @FXML
    private VBox ageDetailsBox;

    // Countdown fields
    @FXML
    private TextField eventNameField;
    @FXML
    private DatePicker eventDatePicker;
    @FXML
    private Label countdownLabel;

    // Work time tracker fields
    @FXML
    private TextField workStartHourField;
    @FXML
    private TextField workStartMinField;
    @FXML
    private TextField workEndHourField;
    @FXML
    private TextField workEndMinField;
    @FXML
    private TextField breakMinutesField;
    @FXML
    private Label workHoursLabel;
    @FXML
    private TableView<WorkLogEntry> workLogTable;
    @FXML
    private TableColumn<WorkLogEntry, String> dateColumn;
    @FXML
    private TableColumn<WorkLogEntry, String> startTimeColumn;
    @FXML
    private TableColumn<WorkLogEntry, String> endTimeColumn;
    @FXML
    private TableColumn<WorkLogEntry, String> breakColumn;
    @FXML
    private TableColumn<WorkLogEntry, String> totalColumn;

    private Timeline timer;
    private int secondsRemaining;
    private ObservableList<WorkLogEntry> workLogEntries = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize conversion dropdown options
        String[] timeUnits = {"Sekunden", "Minuten", "Stunden", "Tage"};
        fromUnitCombo.setItems(FXCollections.observableArrayList(timeUnits));
        toUnitCombo.setItems(FXCollections.observableArrayList(timeUnits));

        // Set numeric text field constraints for time fields
        setNumericOnly(hours1Field, minutes1Field, seconds1Field,
                hours2Field, minutes2Field, seconds2Field,
                timerField, convertInput,
                workStartHourField, workStartMinField,
                workEndHourField, workEndMinField,
                breakMinutesField);

        // Initialize work log table columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        breakColumn.setCellValueFactory(new PropertyValueFactory<>("breakTime"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalWorkTime"));

        workLogTable.setItems(workLogEntries);
    }

    private void setNumericOnly(TextField... fields) {
        for (TextField field : fields) {
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    field.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
        }
    }

    // Time Calculator functions
    @FXML
    protected void onAddButtonClick() {
        try {
            int hours1 = getIntValue(hours1Field);
            int minutes1 = getIntValue(minutes1Field);
            int seconds1 = getIntValue(seconds1Field);

            int hours2 = getIntValue(hours2Field);
            int minutes2 = getIntValue(minutes2Field);
            int seconds2 = getIntValue(seconds2Field);

            int totalSeconds1 = hours1 * 3600 + minutes1 * 60 + seconds1;
            int totalSeconds2 = hours2 * 3600 + minutes2 * 60 + seconds2;

            displayResult(totalSeconds1 + totalSeconds2);
        } catch (NumberFormatException e) {
            resultField.setText("Ungültige Eingabe");
        }
    }

    @FXML
    protected void onSubtractButtonClick() {
        try {
            int hours1 = getIntValue(hours1Field);
            int minutes1 = getIntValue(minutes1Field);
            int seconds1 = getIntValue(seconds1Field);

            int hours2 = getIntValue(hours2Field);
            int minutes2 = getIntValue(minutes2Field);
            int seconds2 = getIntValue(seconds2Field);

            int totalSeconds1 = hours1 * 3600 + minutes1 * 60 + seconds1;
            int totalSeconds2 = hours2 * 3600 + minutes2 * 60 + seconds2;

            displayResult(totalSeconds1 - totalSeconds2);
        } catch (NumberFormatException e) {
            resultField.setText("Ungültige Eingabe");
        }
    }

    private int getIntValue(TextField field) {
        String text = field.getText().trim();
        return text.isEmpty() ? 0 : Integer.parseInt(text);
    }

    private void displayResult(int totalSeconds) {
        int absSeconds = Math.abs(totalSeconds);
        int hours = absSeconds / 3600;
        int minutes = (absSeconds % 3600) / 60;
        int seconds = absSeconds % 60;

        String sign = totalSeconds < 0 ? "-" : "";
        resultField.setText(sign + String.format("%d:%02d:%02d", hours, minutes, seconds));
    }

    @FXML
    protected void onResetButtonClick() {
        hours1Field.clear();
        minutes1Field.clear();
        seconds1Field.clear();
        hours2Field.clear();
        minutes2Field.clear();
        seconds2Field.clear();
        resultField.clear();
    }

    // Timer functions
    @FXML
    protected void onStartTimerButtonClick() {
        if (timer != null) {
            timer.stop();
        }

        try {
            secondsRemaining = Integer.parseInt(timerField.getText().trim());
            updateTimerLabel();

            timer = new Timeline(
                    new KeyFrame(Duration.seconds(1), e -> {
                        secondsRemaining--;
                        updateTimerLabel();
                        if (secondsRemaining <= 0) {
                            timer.stop();
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Timer");
                            alert.setHeaderText(null);
                            alert.setContentText("Zeit ist abgelaufen!");
                            alert.showAndWait();
                        }
                    })
            );
            timer.setCycleCount(Timeline.INDEFINITE);
            timer.play();
        } catch (NumberFormatException e) {
            timerLabel.setText("Ungültige Eingabe");
        }
    }

    @FXML
    protected void onStopTimerButtonClick() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void updateTimerLabel() {
        int hours = secondsRemaining / 3600;
        int minutes = (secondsRemaining % 3600) / 60;
        int seconds = secondsRemaining % 60;

        timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    // Converter functions
    @FXML
    protected void onConvertButtonClick() {
        try {
            String fromUnit = fromUnitCombo.getValue();
            String toUnit = toUnitCombo.getValue();
            double value = Double.parseDouble(convertInput.getText().trim());

            if (fromUnit == null || toUnit == null) {
                convertResult.setText("Bitte Einheiten auswählen");
                return;
            }

            // Convert to seconds first
            double seconds = 0;
            switch (fromUnit) {
                case "Sekunden" -> seconds = value;
                case "Minuten" -> seconds = value * 60;
                case "Stunden" -> seconds = value * 3600;
                case "Tage" -> seconds = value * 86400;
            }

            // Convert from seconds to target unit
            double result = 0;
            switch (toUnit) {
                case "Sekunden" -> result = seconds;
                case "Minuten" -> result = seconds / 60;
                case "Stunden" -> result = seconds / 3600;
                case "Tage" -> result = seconds / 86400;
            }

            convertResult.setText(String.format("Ergebnis: %.2f %s", result, toUnit));
        } catch (NumberFormatException e) {
            convertResult.setText("Ungültige Eingabe");
        }
    }

    // Date to Day of Week functions
    @FXML
    protected void onShowDayOfWeekButtonClick() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate != null) {
            // Get the day of week in German
            String dayOfWeek = selectedDate.getDayOfWeek()
                    .getDisplayName(TextStyle.FULL, Locale.GERMAN);

            // Format the date in German style
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String formattedDate = selectedDate.format(formatter);

            dayOfWeekLabel.setText(formattedDate + " war ein " + dayOfWeek);
        } else {
            dayOfWeekLabel.setText("Bitte wählen Sie ein Datum aus");
        }
    }

    // Age calculator functions
    @FXML
    protected void onCalculateAgeClick() {
        LocalDate birthDate = birthDatePicker.getValue();
        if (birthDate != null) {
            LocalDate now = LocalDate.now();

            // Check if birth date is in the future
            if (birthDate.isAfter(now)) {
                ageLabel.setText("Geburtsdatum kann nicht in der Zukunft liegen!");
                ageDetailsBox.getChildren().clear();
                return;
            }

            Period period = Period.between(birthDate, now);
            long totalDays = ChronoUnit.DAYS.between(birthDate, now);

            ageLabel.setText(String.format(
                    "%d Jahre, %d Monate, %d Tage",
                    period.getYears(), period.getMonths(), period.getDays()
            ));

            // Add more age details
            ageDetailsBox.getChildren().clear();

            Label totalYearsLabel = new Label(String.format("Alter in Jahren: %.2f", totalDays / 365.25));
            Label totalMonthsLabel = new Label(String.format("Alter in Monaten: %.1f", totalDays / 30.44));
            Label totalDaysLabel = new Label(String.format("Alter in Tagen: %d", totalDays));
            Label totalHoursLabel = new Label(String.format("Alter in Stunden: %d", totalDays * 24));

            ageDetailsBox.getChildren().addAll(
                    totalYearsLabel, totalMonthsLabel, totalDaysLabel, totalHoursLabel
            );
        } else {
            ageLabel.setText("Bitte wählen Sie ein Geburtsdatum aus");
            ageDetailsBox.getChildren().clear();
        }
    }

    // Countdown functions
    @FXML
    protected void onCalculateCountdownClick() {
        String eventName = eventNameField.getText();
        if (eventName.isEmpty()) {
            eventName = "dem Event";
        }

        LocalDate eventDate = eventDatePicker.getValue();
        if (eventDate != null) {
            LocalDate today = LocalDate.now();
            long daysUntil = ChronoUnit.DAYS.between(today, eventDate);

            if (daysUntil < 0) {
                countdownLabel.setText(
                        String.format("%s war vor %d Tagen", eventName, Math.abs(daysUntil))
                );
            } else if (daysUntil == 0) {
                countdownLabel.setText(String.format("%s ist heute!", eventName));
            } else {
                countdownLabel.setText(
                        String.format("Noch %d Tage bis %s", daysUntil, eventName)
                );
            }
        } else {
            countdownLabel.setText("Bitte wählen Sie ein Datum aus");
        }
    }

    // Work time tracker functions
    @FXML
    protected void onCalculateWorkHoursClick() {
        try {
            int startHour = getIntValue(workStartHourField);
            int startMin = getIntValue(workStartMinField);
            int endHour = getIntValue(workEndHourField);
            int endMin = getIntValue(workEndMinField);
            int breakMinutes = getIntValue(breakMinutesField);

            // Validate time inputs
            if (startHour > 23 || startMin > 59 || endHour > 23 || endMin > 59) {
                workHoursLabel.setText("Ungültige Zeitangabe");
                return;
            }

            LocalTime startTime = LocalTime.of(startHour, startMin);
            LocalTime endTime = LocalTime.of(endHour, endMin);

            long totalMinutes = startTime.until(endTime, ChronoUnit.MINUTES);

            // Handle times that cross midnight
            if (totalMinutes < 0) {
                totalMinutes += 24 * 60; // Add a full day in minutes
            }

            // Subtract break time
            totalMinutes -= breakMinutes;

            if (totalMinutes < 0) {
                workHoursLabel.setText("Pausenzeit ist länger als Arbeitszeit");
                return;
            }

            int hours = (int) (totalMinutes / 60);
            int minutes = (int) (totalMinutes % 60);

            workHoursLabel.setText(String.format(
                    "Arbeitszeit: %d Stunden, %d Minuten", hours, minutes
            ));

        } catch (Exception e) {
            workHoursLabel.setText("Fehler bei der Berechnung");
        }
    }

    @FXML
    protected void onAddWorkLogEntryClick() {
        try {
            LocalDate today = LocalDate.now();
            int startHour = getIntValue(workStartHourField);
            int startMin = getIntValue(workStartMinField);
            int endHour = getIntValue(workEndHourField);
            int endMin = getIntValue(workEndMinField);
            int breakMinutes = getIntValue(breakMinutesField);

            // Validate time inputs
            if (startHour > 23 || startMin > 59 || endHour > 23 || endMin > 59) {
                showAlert(Alert.AlertType.ERROR, "Ungültige Zeitangabe",
                        "Stunden müssen zwischen 0-23 und Minuten zwischen 0-59 liegen.");
                return;
            }

            String startTime = String.format("%02d:%02d", startHour, startMin);
            String endTime = String.format("%02d:%02d", endHour, endMin);

            // Calculate work time
            LocalTime start = LocalTime.of(startHour, startMin);
            LocalTime end = LocalTime.of(endHour, endMin);

            long totalMinutes = start.until(end, ChronoUnit.MINUTES);
            if (totalMinutes < 0) {
                totalMinutes += 24 * 60; // Add a full day for overnight shifts
            }

            totalMinutes -= breakMinutes;
            if (totalMinutes < 0) {
                showAlert(Alert.AlertType.ERROR, "Fehler", "Pausenzeit ist länger als Arbeitszeit!");
                return;
            }

            int hours = (int) (totalMinutes / 60);
            int minutes = (int) (totalMinutes % 60);
            String totalWorkTime = String.format("%d:%02d", hours, minutes);

            // Add to table
            WorkLogEntry entry = new WorkLogEntry(
                    today.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    startTime,
                    endTime,
                    breakMinutes + " min",
                    totalWorkTime
            );

            workLogEntries.add(entry);
            workLogTable.refresh();

            // Clear fields
            workStartHourField.clear();
            workStartMinField.clear();
            workEndHourField.clear();
            workEndMinField.clear();
            breakMinutesField.clear();
            workHoursLabel.setText("Eintrag hinzugefügt!");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Fehler", "Fehler beim Hinzufügen des Eintrags: " + e.getMessage());
        }
    }

    @FXML
    protected void onDeleteWorkLogEntryClick() {
        WorkLogEntry selectedEntry = workLogTable.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            workLogEntries.remove(selectedEntry);
            workLogTable.refresh();
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Hinweis", "Bitte wählen Sie einen Eintrag zum Löschen aus.");
        }
    }

    // Helper method for showing alerts
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Work log entry class for table
    public static class WorkLogEntry {
        private final String date;
        private final String startTime;
        private final String endTime;
        private final String breakTime;
        private final String totalWorkTime;

        public WorkLogEntry(String date, String startTime, String endTime,
                            String breakTime, String totalWorkTime) {
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.breakTime = breakTime;
            this.totalWorkTime = totalWorkTime;
        }

        public String getDate() {
            return date;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public String getBreakTime() {
            return breakTime;
        }

        public String getTotalWorkTime() {
            return totalWorkTime;
        }
    }
}