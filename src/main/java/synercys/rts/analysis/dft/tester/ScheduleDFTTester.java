package synercys.rts.analysis.dft.tester;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synercys.rts.analysis.dft.ScheduleDFTAnalysisReport;
import synercys.rts.analysis.dft.ScheduleDFTAnalyzer;
import synercys.rts.framework.Task;
import synercys.rts.framework.TaskSet;
import synercys.rts.scheduler.AdvanceableSchedulerInterface;
import synercys.rts.scheduler.AdvanceableSchedulerSimulator;
import synercys.rts.scheduler.SchedulerUtil;
import synercys.rts.util.JsonLogExporter;

public class ScheduleDFTTester {
    private static final Logger loggerConsole = LogManager.getLogger("console");

    AdvanceableSchedulerSimulator scheduler;
    TaskSet taskSet;
    ScheduleDFTAnalysisReport report;
    ScheduleDFTAnalyzer analyzer = new ScheduleDFTAnalyzer();

    public ScheduleDFTTester(TaskSet taskSet, String schedulingPolicy, boolean executionVariation) {
        this.taskSet = taskSet;
        scheduler = SchedulerUtil.getScheduler(schedulingPolicy, taskSet, executionVariation);
        scheduler.setTraceEnabled(true);
        analyzer.setTaskSet(taskSet);
    }

    public ScheduleDFTAnalysisReport run(long simDuration) {
        loggerConsole.info("Used Scheduler: {}", SchedulerUtil.getSchedulerName(scheduler));
        analyzer.setBinarySchedule(scheduler.runSimWithDefaultOffset(simDuration));
        report = analyzer.computeFreqSpectrum();
        return report;
    }

    public void exportReport(String filePath) {
        // Handle the file's full path + base name
        String fileFullPathBasePrefix = FilenameUtils.concat(FilenameUtils.getFullPath(filePath), FilenameUtils.getBaseName(filePath));
        if (FilenameUtils.getBaseName(filePath).isEmpty()) {
            fileFullPathBasePrefix = FilenameUtils.concat(fileFullPathBasePrefix, String.valueOf(taskSet.getId()));
        }

        JsonLogExporter exporter = new JsonLogExporter(fileFullPathBasePrefix + ".rtdft");
        exporter.exportDFTAnalysisReport(report);
    }

    public ScheduleDFTAnalysisReport getReport() {
        return report;
    }

    public AdvanceableSchedulerSimulator getScheduler() {
        return scheduler;
    }
}
