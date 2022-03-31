import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is for analyze the operation
 */
class OptionAnalysis {
	private Option filePathOption;
	private Option increaseOption;
	private Option lastModifiedOption;
	private Option creationOption;
	private Option decreaseOption;
	private Option yearOption;
	private Option monthOption;
	private Option dayOption;
	private Option hourOption;
	private Option minuteOption;
	private Option secondOption;
	private Option helpOption;
	private Options options;
	private HelpFormatter helpFormatter;
	// This set contains options that can not repeat appear in one command
	private Set<Option> repeatOptions;
	// This set contains options that can repeat appear in one command
	private Set<Option> mustHave;

	/**
	 * initial each option and option list
	 */
	OptionAnalysis() {
		filePathOption = Option.builder("f").longOpt("file-path").hasArg(true)
				.argName("file path")
				.desc("specify the path of file")
				.build();
		increaseOption = Option.builder("I").longOpt("increase").hasArg(false)
				.desc("increase file time, it's unit could be year, month, day, hour, minute or second")
				.build();
		// This option use 'D' short name, different from day option
		decreaseOption = Option.builder("D").longOpt("decrease").hasArg(false)
				.desc("decrease file time, it's unit could be year, month, day, hour, minute or second")
				.build();
		lastModifiedOption = Option.builder("l").longOpt("last-time").hasArg(false)
				.desc("specify for modify last modified file time")
				.build();
		creationOption = Option.builder("c").longOpt("create-time").hasArg(false)
				.desc("specify for modify create file time")
				.build();
		yearOption = Option.builder("y").longOpt("year").hasArg(true)
				.argName("number of year")
				.desc("file time operation unit of year")
				.build();
		// This option use 'M' short name, different from minute option
		monthOption = Option.builder("M").longOpt("month").hasArg(true)
				.argName("number of month")
				.desc("file time operation unit of month")
				.build();
		// This option use 'd' short name, different from decrease option
		dayOption = Option.builder("d").longOpt("day").hasArg(true)
				.argName("number of day")
				.desc("file time operation unit of day")
				.build();
		// This option use 'H' short name, different from help option
		hourOption = Option.builder("H").longOpt("hour").hasArg(true)
				.argName("number of hour")
				.desc("file time operation unit of hour")
				.build();
		// This option use 'm' short name, different from month option
		minuteOption = Option.builder("m").longOpt("minute").hasArg(true)
				.argName("number of minute")
				.desc("file time operation unit of minute")
				.build();
		secondOption = Option.builder("s").longOpt("second").hasArg(true)
				.argName("number of second")
				.desc("file time operation unit of second")
				.build();
		// This option use 'h' short name, different from hour option
		helpOption = Option.builder("h").longOpt("help").hasArg(false)
				.desc("show help information")
				.build();
		options = new Options();
		options.addOption(filePathOption);
		options.addOption(increaseOption);
		options.addOption(decreaseOption);
		options.addOption(lastModifiedOption);
		options.addOption(creationOption);
		options.addOption(yearOption);
		options.addOption(monthOption);
		options.addOption(dayOption);
		options.addOption(hourOption);
		options.addOption(minuteOption);
		options.addOption(secondOption);
		options.addOption(helpOption);
		helpFormatter = new HelpFormatter();
		repeatOptions = new HashSet<>();
		repeatOptions.add(increaseOption);
		repeatOptions.add(decreaseOption);
		repeatOptions.add(helpOption);
		mustHave = new HashSet<>();
		mustHave.add(lastModifiedOption);
		mustHave.add(creationOption);
	}

	/**
	 * return an option factory instance of options
	 * @return options of option list
	 */
	public Options getOptions() {
		return options;
	}

	public void printHelp() {
		helpFormatter.printHelp("ftm",
				"""
						This program is used to modify lastModified and creation time of specified file,one of increase option and decrease option must be specified.
						create and last modified option must be specified one or both of them.
						other option is optional.
						example:
						    ftm -il -y 9 -m 1 -d 1, This will increase file last modified time 9 years 1 month and 1 day
						    ftm -Dc -m 10, This will only increase file create time 10 days
						
						""",
				options, """
                        
						Max value of each argument:
						    year : 30
						    month : 360
						    day : 10950
						    hour : 262800
						    minute : 15768000
						    second : 946080000
						
						file time after being modified can't out of range below:
						    [1970-01-01 00:00:00, your current system time]
						time maybe different from on the screen because of time zone, it means that true file time should be time screen plus time against UTC time
						For example:
						2020-03-31T11:00:08Z is the time on your screen, true time on your file attribute should be 2020-03-31T19:00:08Z if your time zone is UTC+8
						
						issue report: https://github.com/fresh-bitbird/file_modify/issues.""",
				true);
	}

	public Set<Option> getRepeatOptions() {
		return repeatOptions;
	}

	public Set<Option> getMustHave() {
		return mustHave;
	}
}
