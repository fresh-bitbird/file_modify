# file time modifier

`ftm` is a tool to modify creation time and last modified
time of a file or directory. Time units that could be modified
as below:

| year | month |  day  |  hour  |  minute  |   second  |
|:----:|:-----:|:-----:|:------:|:--------:|:---------:|
|  30  |  360  | 10950 | 262800 | 15768000 | 946080000 |

**Notes** : first row is unit of time, second row is 
maximum number allowed.

Time attribute allowed to be modified:

| last modified time | creation time |
| ------------------ | ------------- |

## Install
Download using `wget` from GitHub release

**exe**:
```
wget https://github.com/fresh-bitbird/file_modify/releases/download/latest/ftm.exe
```

**jar**:

```
wget https://github.com/fresh-bitbird/file_modify/releases/download/latest/file_modify.jar
```

## Usage

You can use `ftm` modify file time.

### Decrease file last modified time

```
ftm -Dld [decreaded-day-number] -f [file-name]
```

### Increase file creation time

```
ftm -IcH [increased-hour-number] -f [file-name]
```

## Time Zone

File time when `ftm` shown is Standard UTC time format,
it means if your time zone is different from UTC+0, it
might not same as your true system time.

**For example**

If you use:

```
ftm -Dld 1 -f [file-name]
```

result will be

```
current last modified: 2022-01-31T14:26:53.6314489Z
modified last modified: 2022-01-30T14:26:53.6314489Z
```

but actually your last modified time before modified is
`2022-01-31T22:26:53.6314489Z`, this is because your time-zone
is UTC+8, so result shown should be
`[true system time] - 8`

## Limits

### Changing number each time

Same as above

### Time range after modifying

From `1970-01-01 00:00:00` to `current system time`

## License

GPL

## Issue report

It's just a tool to modify file time from my impromptu
idea. If there is any error, I'd like to receive advice
and contribution. Thanks!
