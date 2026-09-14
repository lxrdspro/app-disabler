package com.example.appdisabler;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import dev.rikka.shizuku.Shizuku;
import dev.rikka.shizuku.ShizukuProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String PERMISSION = "moe.shizuku.privileged.api";
    private static final String PROTECTED_PACKAGE = "com.example.appdisabler";
    private static final String PROTECTED_SHIZUKU = "moe.shizuku.privileged.api";

    private TextView shizukuStatus;
    private Spinner userSpinner;
    private EditText searchBox;
    private Spinner filterSpinner;
    private LinearLayout packageListContainer;
    private Button selectAllBtn;
    private Button clearBtn;
    private Button enableBtn;
    private Button disableBtn;
    private TextView progressText;

    private PackageManager packageManager;
    private Handler mainHandler;

    private List<String> users = new ArrayList<>();
    private Map<Integer, List<String>> userPackages = new HashMap<>();
    private Set<String> selectedPackages = new HashSet<>();
    private List<String> currentDisplayedPackages = new ArrayList<>();

    private int currentUserId = 0;
    private String currentFilter = "all";
    private String currentSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        packageManager = getPackageManager();
        mainHandler = new Handler(Looper.getMainLooper());

        initViews();
        setupListeners();
        checkShizukuStatus();
        loadUsers();
    }

    private void initViews() {
        shizukuStatus = findViewById(R.id.shizuku_status);
        userSpinner = findViewById(R.id.user_spinner);
        searchBox = findViewById(R.id.search_box);
        filterSpinner = findViewById(R.id.filter_spinner);
        packageListContainer = findViewById(R.id.package_list_container);
        selectAllBtn = findViewById(R.id.btn_select_all);
        clearBtn = findViewById(R.id.btn_clear);
        enableBtn = findViewById(R.id.btn_enable);
        disableBtn = findViewById(R.id.btn_disable);
        progressText = findViewById(R.id.progress_text);

        setupFilterSpinner();
    }

    private void setupFilterSpinner() {
        String[] filters = {getString(R.string.filter_all), getString(R.string.filter_third_party), getString(R.string.filter_system)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        userSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position >= 0 && position < users.size()) {
                    String userStr = users.get(position);
                    currentUserId = extractUserId(userStr);
                    selectedPackages.clear();
                    loadPackagesForUser(currentUserId);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearch = s.toString().toLowerCase();
                updatePackageList();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        filterSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                switch (position) {
                    case 0:
                        currentFilter = "all";
                        break;
                    case 1:
                        currentFilter = "third_party";
                        break;
                    case 2:
                        currentFilter = "system";
                        break;
                }
                updatePackageList();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        selectAllBtn.setOnClickListener(v -> selectAllPackages());
        clearBtn.setOnClickListener(v -> clearSelection());
        enableBtn.setOnClickListener(v -> performBulkAction("enable"));
        disableBtn.setOnClickListener(v -> performBulkAction("disable"));
    }

    private void checkShizukuStatus() {
        new Thread(() -> {
            try {
                if (!isShizukuInstalled()) {
                    updateShizukuStatus("Shizuku: Not installed");
                    return;
                }

                if (!isShizukuRunning()) {
                    updateShizukuStatus("Shizuku: Not running");
                    return;
                }

                if (hasShizukuPermission()) {
                    updateShizukuStatus("Shizuku: Connected");
                } else {
                    updateShizukuStatus("Shizuku: Permission denied - Request permission");
                    requestShizukuPermission();
                }
            } catch (Exception e) {
                updateShizukuStatus("Shizuku: Error - " + e.getMessage());
            }
        }).start();
    }

    private boolean isShizukuInstalled() {
        try {
            packageManager.getPackageInfo("moe.shizuku.privileged.api", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean isShizukuRunning() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return false;

        for (ActivityManager.RunningServiceInfo service : am.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getPackageName().equals("moe.shizuku.privileged.api")) {
                return true;
            }
        }
        return false;
    }

    private boolean hasShizukuPermission() {
        try {
            return Shizuku.checkSelfPermission(PERMISSION) == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }

    private void requestShizukuPermission() {
        try {
            Shizuku.requestPermission(PERMISSION);
        } catch (Exception e) {
            showToast("Permission request failed: " + e.getMessage());
        }
    }

    private void updateShizukuStatus(String status) {
        mainHandler.post(() -> shizukuStatus.setText(status));
    }

    private void loadUsers() {
        new Thread(() -> {
            try {
                String output = executeCommand("cmd user list");
                if (output == null || output.isEmpty()) {
                    mainHandler.post(() -> {
                        users.clear();
                        users.add("User 0 — Owner");
                        updateUserSpinner();
                    });
                    return;
                }

                users.clear();
                String[] lines = output.split("\n");
                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.contains("Users:")) {
                        users.add(line);
                    }
                }

                if (users.isEmpty()) {
                    users.add("User 0 — Owner");
                }

                mainHandler.post(this::updateUserSpinner);
            } catch (Exception e) {
                showToast("Error loading users: " + e.getMessage());
            }
        }).start();
    }

    private void updateUserSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(adapter);
    }

    private int extractUserId(String userStr) {
        try {
            String[] parts = userStr.split("\\s+");
            if (parts.length > 1) {
                return Integer.parseInt(parts[1]);
            }
        } catch (Exception e) {
            // ignore
        }
        return 0;
    }

    private void loadPackagesForUser(int userId) {
        new Thread(() -> {
            try {
                String output = executeCommand("pm list packages --user " + userId);
                if (output == null) {
                    mainHandler.post(() -> showPackageError("User " + userId + " is inaccessible: Shell does not have permission to access this user."));
                    return;
                }

                List<String> packages = new ArrayList<>();
                String[] lines = output.split("\n");
                for (String line : lines) {
                    line = line.trim();
                    if (line.startsWith("package:")) {
                        packages.add(line.substring(8));
                    }
                }

                Collections.sort(packages);
                userPackages.put(userId, packages);
                mainHandler.post(this::updatePackageList);
            } catch (Exception e) {
                mainHandler.post(() -> showPackageError("Error loading packages: " + e.getMessage()));
            }
        }).start();
    }

    private void updatePackageList() {
        packageListContainer.removeAllViews();
        selectedPackages.clear();
        currentDisplayedPackages.clear();

        List<String> packages = userPackages.getOrDefault(currentUserId, new ArrayList<>());

        for (String pkg : packages) {
            if (shouldDisplayPackage(pkg)) {
                currentDisplayedPackages.add(pkg);
                addPackageCheckbox(pkg);
            }
        }

        if (currentDisplayedPackages.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No packages found");
            emptyText.setPadding(16, 16, 16, 16);
            packageListContainer.addView(emptyText);
        }
    }

    private boolean shouldDisplayPackage(String packageName) {
        if (!packageName.toLowerCase().contains(currentSearch)) {
            return false;
        }

        if ("all".equals(currentFilter)) {
            return true;
        }

        try {
            ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
            boolean isSystemApp = (info.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

            if ("system".equals(currentFilter)) {
                return isSystemApp;
            } else if ("third_party".equals(currentFilter)) {
                return !isSystemApp;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }

        return true;
    }

    private void addPackageCheckbox(String packageName) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(packageName);
        checkBox.setPadding(16, 8, 16, 8);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (packageName.equals(PROTECTED_PACKAGE) || packageName.equals(PROTECTED_SHIZUKU)) {
                    checkBox.setChecked(false);
                    showToast("Cannot disable protected package: " + packageName);
                } else {
                    selectedPackages.add(packageName);
                }
            } else {
                selectedPackages.remove(packageName);
            }
        });

        packageListContainer.addView(checkBox);
    }

    private void selectAllPackages() {
        for (String pkg : currentDisplayedPackages) {
            if (!pkg.equals(PROTECTED_PACKAGE) && !pkg.equals(PROTECTED_SHIZUKU)) {
                selectedPackages.add(pkg);
            }
        }
        updatePackageList();
    }

    private void clearSelection() {
        selectedPackages.clear();
        updatePackageList();
    }

    private void performBulkAction(String action) {
        if (selectedPackages.isEmpty()) {
            showToast("No packages selected");
            return;
        }

        new Thread(() -> {
            int total = selectedPackages.size();
            int completed = 0;
            int failed = 0;

            for (String pkg : selectedPackages) {
                completed++;
                String progress = "Processing " + completed + " of " + total + "...";
                mainHandler.post(() -> progressText.setText(progress));

                try {
                    String cmd = "enable".equals(action)
                            ? "pm enable --user " + currentUserId + " " + pkg
                            : "pm disable-user --user " + currentUserId + " " + pkg;

                    String result = executeCommand(cmd);
                    if (result == null || result.contains("Error")) {
                        failed++;
                    }
                } catch (Exception e) {
                    failed++;
                }
            }

            int finalFailed = failed;
            int finalCompleted = completed - failed;
            mainHandler.post(() -> {
                String actionLabel = "enable".equals(action) ? "Enabled" : "Disabled";
                progressText.setText("Completed — " + actionLabel + ": " + finalCompleted + " | Failed: " + finalFailed);
                selectedPackages.clear();
                loadPackagesForUser(currentUserId);
            });
        }).start();
    }

    private void showPackageError(String message) {
        packageListContainer.removeAllViews();
        TextView errorText = new TextView(this);
        errorText.setText(message);
        errorText.setPadding(16, 16, 16, 16);
        errorText.setTextColor(0xFFFF0000);
        packageListContainer.addView(errorText);
    }

    private String executeCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            reader.close();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                StringBuilder errorOutput = new StringBuilder();
                while ((line = errorReader.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                }
                errorReader.close();

                if (errorOutput.length() > 0) {
                    return null;
                }
            }

            return output.toString().trim();
        } catch (Exception e) {
            return null;
        }
    }

    private void showToast(String message) {
        mainHandler.post(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
    }
}
