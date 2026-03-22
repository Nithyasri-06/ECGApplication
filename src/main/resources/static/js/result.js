document.addEventListener("DOMContentLoaded", function () {

    const params = new URLSearchParams(window.location.search);
    const recordId = params.get("id");

    if (recordId) {

        // ===== VIEW STORED RECORD =====

        fetch(`http://localhost:8080/getRecordById/${recordId}`)
            .then(res => {
                if (!res.ok) throw new Error("Record not found");
                return res.json();
            })
            .then(data => {

                if (!data || !data.analysis || !data.signal) {
                    alert("No valid ECG data found");
                    window.location.href = "ecg-records.html";
                    return;
                }

                displayResult(data);

            })
            .catch(err => {
                console.error(err);
                alert("Server error while loading ECG report");
                window.location.href = "ecg-records.html";
            });

    } else {

        // ===== UPLOAD RESULT FLOW =====

        let result = localStorage.getItem("ecgResult");

        if (!result) {
            alert("No ECG result found");
            window.location.href = "ecg-upload.html";
            return;
        }

        result = JSON.parse(result);

        if (!result || !result.signal || !result.analysis) {
            alert("No valid ECG data found");
            window.location.href = "ecg-upload.html";
            return;
        }

        displayResult(result);
    }
});


// ================= DISPLAY ECG REPORT =================

function displayResult(result) {
    console.log("ECG Result Data:", result);
    const analysis = result.analysis;
    let patientId = result.patientId || localStorage.getItem("patientId");

        document.getElementById("patientId").innerText =
            result.patientId || "Unknown";


        // ===== Report Date =====

        document.getElementById("reportDate").innerText =
            new Date().toLocaleDateString("en-GB");

    // ===== Signal Analysis =====

    document.getElementById("heartRate").innerText =
        analysis.signal_analysis?.heart_rate || "N/A";

    document.getElementById("rhythm").innerText =
        analysis.signal_analysis?.rhythm_category || "N/A";

    document.getElementById("totalBeats").innerText =
        analysis.signal_analysis?.total_beats || "N/A";

    document.getElementById("abnormalBeats").innerText =
        analysis.signal_analysis?.abnormal_beats || "0";


    // ===== Beat Distribution =====

    const morph = analysis.morphological_analysis || {};

    let distributionText = "Normal";

    if (morph.abnormal_beat_distribution &&
        Object.keys(morph.abnormal_beat_distribution).length > 0) {

        distributionText = Object.entries(morph.abnormal_beat_distribution)
            .map(([type, count]) => `${type}: ${count}`)
            .join(", ");
    }

    document.getElementById("beatDistribution").innerText = distributionText;


    // ===== Model Confidence =====

    //document.getElementById("confidence").innerText =

        morph.model_confidence || "N/A";


    // ===== Clinical Interpretation =====

    document.getElementById("status").innerText =
        analysis.clinical_interpretation?.cardiac_status || "N/A";

    document.getElementById("risk").innerText =
        analysis.clinical_interpretation?.risk_level || "N/A";

    document.getElementById("recommendation").innerText =
        analysis.clinical_interpretation?.recommendation || "N/A";


    // ===== Clinical Findings =====

    let findingsText = "None";

    if (analysis.clinical_interpretation?.additional_findings) {

        findingsText = analysis.clinical_interpretation.additional_findings.join(", ");
    }

    document.getElementById("findings").innerText = findingsText;



    // ================= ECG CHART =================

    const rawSignal = result.signal;

    const fs = result.sampling_rate || 360;

    const displaySeconds = 2;
    const maxSamples = fs * displaySeconds;

    const displaySignal = rawSignal;

    const ecgSignal = displaySignal.map((val, idx) => ({
        x: idx / fs,
        y: val
    }));

    const ctx = document.getElementById("ecgChart").getContext("2d");

    new Chart(ctx, {

        type: "line",

        data: {
            datasets: [{
                label: "ECG Signal",
                data: ecgSignal,
                borderWidth: 1.5,
                pointRadius: 0,
                tension: 0.25
            }]
        },

        options: {
            responsive: true,
            animation: false,
            parsing: false,

            scales: {

                x: {
                    type: "linear",
                    title: {
                        display: true,
                        text: "Time (seconds)"
                    },
                    min: 0,
                    max: displaySeconds
                },

                y: {
                    title: {
                        display: true,
                        text: "Amplitude (mV)"
                    }
                }
            }
        }
    });
}


// ===== BACK BUTTON =====

function goHome() {
    window.location.href = "index.html";
}