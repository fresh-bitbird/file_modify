import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * @author Wenyang Tang
 * <p>
 * This class is for modifying the time stamp of a file on Windows.
 * The time is based on original file time, It means time you can increase or decrease
 * is from creation time and last modified time, last modified time in default.
 * <p>
 * The inner representation is Instant
 * <p>
 * user can input positive value and negative value,
 * but their value can't beyond Max values below.
 * MAX value of Each change:
 * year : 30
 * month : 360
 * day : 10950
 * hour : 262800
 * minute : 15768000
 * second : 946080000
 * It should ask user do not make file time stamp exceed range below:
 * range : [1970-01-01 00:00:00 -- current time]
 * That means the earliest time should not earlier than 1970-01-01 00:00:00
 * and latest time should not later than current time from system.
 */
public class FileTimeMod {

	Path filePath;

	/**
	 * Max values are allowed to input
	 */
	public final long MAX_YEAR = 30;
	public final long MAX_MONTH = 360; // 30 * 12
	public final long MAX_DAY = 10950; // 30 * 365
	public final long MAX_HOUR = 262800; // 30 * 365 * 24
	public final long MAX_MINUTE = 15768000; // 262800 * 60
	public final long MAX_SECOND = 946080000; // 15768000 * 60

	private enum TimeUnit {
		YEAR,
		MONTH,
		DAY,
		HOUR,
		MINUTE,
		SECOND
	}

	private enum OPERATION {
		INCREASE,
		DECREASE
	}

	public static void main(String @NotNull [] args) {
		try {
			process(args);
		} catch (ParseException e) {
			System.err.println("Parsing failed: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Get file time failed: " + e.getMessage());
		} catch (InvalidPathException e) {
			System.err.println("Get file failed, check file path : " + e.getMessage());
		}
	}

	static void process(String[] args) throws ParseException, IOException {
		OptionAnalysis optionAnalysis = new OptionAnalysis();
		Options options = optionAnalysis.getOptions();
		CommandLineParser commandLineParser = new DefaultParser();
		CommandLine commandLine = commandLineParser.parse(options, args);
		FileTimeMod fileTimeMod = new FileTimeMod();
		if (!fileTimeMod.checkArgNum(commandLine, optionAnalysis)) {
			optionAnalysis.printHelp();
			return;
		}
		if (commandLine.hasOption("increase") && commandLine.hasOption("file-path")) {
			fileTimeMod.filePath = Path.of(commandLine.getOptionValue("file-path"));
			if (commandLine.hasOption("last-time")) {
				FileTime currentTime = fileTimeMod.getLastModifiedTime();
				System.out.println("current last modified: " + currentTime);
				currentTime = fileTimeMod.modify(OPERATION.INCREASE, commandLine, currentTime);
				if (!fileTimeMod.inRange(currentTime)) {
					System.out.println("time after modified is out of range, can't modify");
				}
				fileTimeMod.setLastModifiedTime(currentTime);
				System.out.println("modified last modified: " + currentTime);
			}
			if (commandLine.hasOption("create-time")) {
				FileTime currentTime = fileTimeMod.getLastModifiedTime();
				System.out.println("current last modified: " + currentTime);
				currentTime = fileTimeMod.modify(OPERATION.INCREASE, commandLine, currentTime);
				if (!fileTimeMod.inRange(currentTime)) {
					System.out.println("time after modified is out of range, can't modify");
				}
				fileTimeMod.setCreationTime(currentTime);
				System.out.println("modified last modified: " + currentTime);
			}
		} else if (commandLine.hasOption("decrease") && commandLine.hasOption("file-path")) {
			fileTimeMod.filePath = Path.of(commandLine.getOptionValue("file-path"));
			if (commandLine.hasOption("last-time")) {
				FileTime currentTime = fileTimeMod.getLastModifiedTime();
				System.out.println("current last modified: " + currentTime);
				currentTime = fileTimeMod.modify(OPERATION.DECREASE, commandLine, currentTime);
				if (!fileTimeMod.inRange(currentTime)) {
					System.out.println("time after modified is out of range, can't modify");
				}
				fileTimeMod.setLastModifiedTime(currentTime);
				System.out.println("modified last modified: " + currentTime);
			}
			if (commandLine.hasOption("create-time")) {
				FileTime currentTime = fileTimeMod.getLastModifiedTime();
				System.out.println("current last modified: " + currentTime);
				currentTime = fileTimeMod.modify(OPERATION.DECREASE, commandLine, currentTime);
				if (!fileTimeMod.inRange(currentTime)) {
					System.out.println("time after modified is out of range, can't modify");
				}
				fileTimeMod.setCreationTime(currentTime);
				System.out.println("modified last modified: " + currentTime);
			}
		} else if (commandLine.hasOption("help")) {
			optionAnalysis.printHelp();
		}
	}

	private FileTime modify(OPERATION operation, CommandLine commandLine, FileTime fileTime) {
		if (operation == OPERATION.INCREASE) {
			// start to modify time
			if (commandLine.hasOption("year")
					&& isValid(commandLine.getOptionValue("year"), TimeUnit.YEAR)) {
				int year = Integer.parseInt(commandLine.getOptionValue("year"));
				fileTime = increaseYear(year, fileTime);
			}
			if (commandLine.hasOption("month")
					&& isValid(commandLine.getOptionValue("month"), TimeUnit.MONTH)) {
				int month = Integer.parseInt(commandLine.getOptionValue("month"));
				fileTime = increaseMonth(month, fileTime);
			}
			if (commandLine.hasOption("day")
					&& isValid(commandLine.getOptionValue("day"), TimeUnit.DAY)) {
				int day = Integer.parseInt(commandLine.getOptionValue("day"));
				fileTime = increaseDay(day, fileTime);
			}
			if (commandLine.hasOption("hour")
					&& isValid(commandLine.getOptionValue("hour"), TimeUnit.HOUR)) {
				int hour = Integer.parseInt(commandLine.getOptionValue("hour"));
				fileTime = increaseHour(hour, fileTime);
			}
			if (commandLine.hasOption("minute")
					&& isValid(commandLine.getOptionValue("minute"), TimeUnit.MINUTE)) {
				int minute = Integer.parseInt(commandLine.getOptionValue("minute"));
				fileTime = increaseMinute(minute, fileTime);
			}
			if (commandLine.hasOption("second")
					&& isValid(commandLine.getOptionValue("second"), TimeUnit.SECOND)) {
				int second = Integer.parseInt(commandLine.getOptionValue("second"));
				fileTime = increaseSecond(second, fileTime);
			}
		} else {
			// start to modify time
			if (commandLine.hasOption("year")
					&& isValid(commandLine.getOptionValue("year"), TimeUnit.YEAR)) {
				int year = Integer.parseInt(commandLine.getOptionValue("year"));
				fileTime = decreaseYear(year, fileTime);
			}
			if (commandLine.hasOption("month")
					&& isValid(commandLine.getOptionValue("month"), TimeUnit.MONTH)) {
				int month = Integer.parseInt(commandLine.getOptionValue("month"));
				fileTime = decreaseMonth(month, fileTime);
			}
			if (commandLine.hasOption("day")
					&& isValid(commandLine.getOptionValue("day"), TimeUnit.DAY)) {
				int day = Integer.parseInt(commandLine.getOptionValue("day"));
				fileTime = decreaseDay(day, fileTime);
			}
			if (commandLine.hasOption("hour")
					&& isValid(commandLine.getOptionValue("hour"), TimeUnit.HOUR)) {
				int hour = Integer.parseInt(commandLine.getOptionValue("hour"));
				fileTime = decreaseHour(hour, fileTime);
			}
			if (commandLine.hasOption("minute")
					&& isValid(commandLine.getOptionValue("minute"), TimeUnit.MINUTE)) {
				int minute = Integer.parseInt(commandLine.getOptionValue("minute"));
				fileTime = decreaseMinute(minute, fileTime);
			}
			if (commandLine.hasOption("second")
					&& isValid(commandLine.getOptionValue("second"), TimeUnit.SECOND)) {
				int second = Integer.parseInt(commandLine.getOptionValue("second"));
				fileTime = decreaseSecond(second, fileTime);
			}
			// check range of modified time
		}
		return fileTime;
	}

	/**
	 * check if commands from user follow the rule
	 *
	 * @param commandLine    command(s) from arguments
	 * @param optionAnalysis include option analysis tools
	 * @return true if arguments follow the rule, or false
	 */
	boolean checkArgNum(CommandLine commandLine, OptionAnalysis optionAnalysis) {
		int count = 0;
		Set<Option> checked = optionAnalysis.getRepeatOptions();
		for (Option option : checked) {
			if (commandLine.hasOption(option)) {
				count++;
			}
			// count must greater than 0, meanwhile less than 2
			if (count > 1) {
				return false;
			}
		}
		if (count == 0) {
			return false;
		}
		checked = optionAnalysis.getMustHave();
		for (Option option : checked) {
			if (commandLine.hasOption(option)) {
				count++;
			}
		}
		return count > 1;
	}

	static FileTime getCurrentDateTime() {
		return FileTime.from(Instant.now());
	}

	FileTime getLastModifiedTime() throws IOException {
		return Files.getLastModifiedTime(filePath);
	}

	FileTime getCreationTime() throws IOException {
		return (FileTime) Files.getAttribute(filePath, "basic:creationTime");
	}

	void setLastModifiedTime(FileTime modifyTime) throws IOException {
		FileTime currentTime = getLastModifiedTime();
		Files.setAttribute(filePath, "basic:lastModifiedTime", modifyTime);
	}

	void setCreationTime(FileTime modifyTime) throws IOException {
		FileTime currentTime = getCreationTime();
		Files.setAttribute(filePath, "basic:creationTime", modifyTime);
	}

	boolean checkNumValid(String num) {
		return Pattern.matches("[0-9]+", num);
	}

	/**
	 * check if the time user input is valid.
	 *
	 * @param inputTime time from user input
	 * @param timeUnit  unit of time from enum {@code TimeUnit}
	 * @return true if input can be accepted, otherwise, false
	 */
	boolean isValid(String inputTime, TimeUnit timeUnit) {
		if (!checkNumValid(inputTime)) {
			return false;
		}
		int time = Integer.parseInt(inputTime);
		switch (timeUnit) {
			case YEAR -> {
				if (Math.abs(time) > MAX_YEAR) {
					return false;
				}
			}
			case MONTH -> {
				if (Math.abs(time) > MAX_MONTH) {
					return false;
				}
			}
			case DAY -> {
				if (Math.abs(time) > MAX_DAY) {
					return false;
				}
			}
			case HOUR -> {
				if (Math.abs(time) > MAX_HOUR) {
					return false;
				}
			}
			case MINUTE -> {
				if (Math.abs(time) > MAX_MINUTE) {
					return false;
				}
			}
			case SECOND -> {
				if (Math.abs(time) > MAX_SECOND) {
					return false;
				}
			}
			default -> {
				return false;
			}
		}
		return true;
	}

	/**
	 * check if the specified file time is in range
	 *
	 * @param fileTime file time which is checked whether in range from earliest to latest
	 * @return true if is in range, otherwise, false
	 */
	boolean inRange(FileTime fileTime) {
		Instant earliest = Instant.ofEpochMilli(0L);
		Instant latest = Instant.now();
		Instant fileInstant = fileTime.toInstant();
		return fileInstant.isAfter(earliest) && fileInstant.isBefore(latest);
	}

	/**
	 * @param second   second number needs to increase
	 * @param fileTime file time needs to be increased
	 * @return file time after increasing
	 */
	FileTime increaseSecond(int second, FileTime fileTime) {
		Instant newTime = fileTime.toInstant().plus(second, ChronoUnit.SECONDS);
		return FileTime.from(newTime);
	}

	/**
	 * @param minute   minute number needs to increase
	 * @param fileTime file time needs to be increased
	 * @return file time after increasing
	 */
	FileTime increaseMinute(int minute, FileTime fileTime) {
		Instant newTime = fileTime.toInstant().plus(minute, ChronoUnit.MINUTES);
		return FileTime.from(newTime);
	}

	/**
	 * @param hour     hour number needs to increase
	 * @param fileTime file time needs to be increased
	 * @return file time after increasing
	 */
	FileTime increaseHour(int hour, FileTime fileTime) {
		Instant newTime = fileTime.toInstant().plus(hour, ChronoUnit.HOURS);
		return FileTime.from(newTime);
	}

	/**
	 * @param day      day number needs to increase
	 * @param fileTime file time needs to be increased
	 * @return file time after increasing
	 */
	FileTime increaseDay(int day, FileTime fileTime) {
		Instant newTime = fileTime.toInstant().plus(day, ChronoUnit.DAYS);
		return FileTime.from(newTime);
	}

	/**
	 * The unit is 30 days, that means increase one month equalling increase 30 days.
	 * It may cause the deviation from true calendar.
	 *
	 * @param month    month number needs to increase
	 * @param fileTime file time needs to be increased
	 * @return file time after increasing
	 */
	FileTime increaseMonth(int month, FileTime fileTime) {
		Instant newTime = fileTime.toInstant().plus(month * 30L, ChronoUnit.DAYS);
		return FileTime.from(newTime);
	}

	/**
	 * The unit is 365 days, that means increase one year equalling increase 365 days.
	 * It may cause the deviation from true calendar.
	 *
	 * @param year     year number needs to increase
	 * @param fileTime file time needs to be increased
	 * @return file time after increasing
	 */
	FileTime increaseYear(int year, FileTime fileTime) {
		Instant newTime = fileTime.toInstant().plus(year * 365L, ChronoUnit.DAYS);
		return FileTime.from(newTime);
	}

	FileTime decreaseSecond(int second, FileTime fileTime) {
		Instant newTime = fileTime.toInstant().minus(second, ChronoUnit.SECONDS);
		return FileTime.from(newTime);
	}

	FileTime decreaseMinute(int minute, FileTime fileTime) {
		Instant newTime = fileTime.toInstant().minus(minute, ChronoUnit.MINUTES);
		return FileTime.from(newTime);
	}

	FileTime decreaseHour(int hour, FileTime fileTime) {
		Instant newTime = fileTime.toInstant().minus(hour, ChronoUnit.HOURS);
		return FileTime.from(newTime);
	}

	FileTime decreaseDay(int day, FileTime fileTime) {
		Instant newTime = fileTime.toInstant().minus(day, ChronoUnit.DAYS);
		return FileTime.from(newTime);
	}

	/**
	 * The unit is 30 days, that means decrease one month equalling decrease 30 days.
	 * It may cause the deviation from true calendar.
	 *
	 * @param month    month number needs to decrease
	 * @param fileTime file time needs to be decreased
	 * @return file time after decreasing
	 */
	FileTime decreaseMonth(int month, FileTime fileTime) {
		Instant newTime = fileTime.toInstant().minus(month * 30L, ChronoUnit.DAYS);
		return FileTime.from(newTime);
	}

	/**
	 * The unit is 365 days, that means decrease one year equalling decrease 365 days.
	 * It may cause the deviation from true calendar.
	 *
	 * @param year     year number needs to decrease
	 * @param fileTime file time needs to be decreased
	 * @return file time after decreasing
	 */
	FileTime decreaseYear(int year, FileTime fileTime) {
		Instant newTime = fileTime.toInstant().minus(year * 365L, ChronoUnit.DAYS);
		return FileTime.from(newTime);
	}
}
