-- =====================================================
-- SEED DATA
-- =====================================================

INSERT INTO MedicalFacilities (id, name, type, latitude, longitude, email, phone)
SELECT 1, 'VIX Central Hospital', 'HOSPITAL', 45.4642, 9.1900, 'central@vixhealth.com', '+390200000001'
WHERE NOT EXISTS (SELECT 1 FROM MedicalFacilities WHERE id = 1);

INSERT INTO MedicalFacilities (id, name, type, latitude, longitude, email, phone)
SELECT 2, 'VIX North Clinic', 'MEDICAL_FACILITY', 45.5000, 9.2100, 'north@vixhealth.com', '+390200000002'
WHERE NOT EXISTS (SELECT 1 FROM MedicalFacilities WHERE id = 2);

INSERT INTO MedicalFacilities (id, name, type, latitude, longitude, email, phone)
SELECT 3, 'VIX Diagnostic Center', 'MEDICAL_FACILITY', 45.4300, 9.1700, 'diagnostic@vixhealth.com', '+390200000003'
WHERE NOT EXISTS (SELECT 1 FROM MedicalFacilities WHERE id = 3);

INSERT INTO Storages (id, facility_id)
SELECT 1, 1 WHERE NOT EXISTS (SELECT 1 FROM Storages WHERE id = 1);

INSERT INTO Storages (id, facility_id)
SELECT 2, 2 WHERE NOT EXISTS (SELECT 1 FROM Storages WHERE id = 2);

INSERT INTO Storages (id, facility_id)
SELECT 3, 3 WHERE NOT EXISTS (SELECT 1 FROM Storages WHERE id = 3);

INSERT INTO Resources (id, name, description, price)
SELECT 1, 'Syringes', 'Disposable sterile syringes', 0.50
WHERE NOT EXISTS (SELECT 1 FROM Resources WHERE id = 1);

INSERT INTO Resources (id, name, description, price)
SELECT 2, 'Gloves', 'Disposable medical gloves', 0.20
WHERE NOT EXISTS (SELECT 1 FROM Resources WHERE id = 2);

INSERT INTO Resources (id, name, description, price)
SELECT 3, 'Bandages', 'Sterile bandages', 1.00
WHERE NOT EXISTS (SELECT 1 FROM Resources WHERE id = 3);

INSERT INTO Resources (id, name, description, price)
SELECT 4, 'Masks', 'Surgical masks', 0.30
WHERE NOT EXISTS (SELECT 1 FROM Resources WHERE id = 4);

INSERT INTO Resources (id, name, description, price)
SELECT 5, 'Disinfectant', 'Medical surface disinfectant', 4.50
WHERE NOT EXISTS (SELECT 1 FROM Resources WHERE id = 5);

INSERT INTO Resources (id, name, description, price)
SELECT 6, 'Gauze', 'Sterile gauze pads', 0.80
WHERE NOT EXISTS (SELECT 1 FROM Resources WHERE id = 6);

INSERT INTO Resources (id, name, description, price)
SELECT 7, 'IV Bags', 'Intravenous fluid bags', 3.20
WHERE NOT EXISTS (SELECT 1 FROM Resources WHERE id = 7);

INSERT INTO Resources (id, name, description, price)
SELECT 8, 'Thermometers', 'Digital thermometers', 12.00
WHERE NOT EXISTS (SELECT 1 FROM Resources WHERE id = 8);

INSERT INTO Resources (id, name, description, price)
SELECT 9, 'Test Tubes', 'Laboratory test tubes', 0.40
WHERE NOT EXISTS (SELECT 1 FROM Resources WHERE id = 9);

INSERT INTO StorageResources (storage_id, resource_id, quantity)
SELECT 1, 1, 500 WHERE NOT EXISTS (SELECT 1 FROM StorageResources WHERE storage_id = 1 AND resource_id = 1);

INSERT INTO StorageResources (storage_id, resource_id, quantity)
SELECT 1, 2, 1000 WHERE NOT EXISTS (SELECT 1 FROM StorageResources WHERE storage_id = 1 AND resource_id = 2);

INSERT INTO StorageResources (storage_id, resource_id, quantity)
SELECT 1, 3, 250 WHERE NOT EXISTS (SELECT 1 FROM StorageResources WHERE storage_id = 1 AND resource_id = 3);

INSERT INTO StorageResources (storage_id, resource_id, quantity)
SELECT 2, 4, 600 WHERE NOT EXISTS (SELECT 1 FROM StorageResources WHERE storage_id = 2 AND resource_id = 4);

INSERT INTO StorageResources (storage_id, resource_id, quantity)
SELECT 2, 5, 80 WHERE NOT EXISTS (SELECT 1 FROM StorageResources WHERE storage_id = 2 AND resource_id = 5);

INSERT INTO StorageResources (storage_id, resource_id, quantity)
SELECT 2, 6, 300 WHERE NOT EXISTS (SELECT 1 FROM StorageResources WHERE storage_id = 2 AND resource_id = 6);

INSERT INTO StorageResources (storage_id, resource_id, quantity)
SELECT 3, 7, 120 WHERE NOT EXISTS (SELECT 1 FROM StorageResources WHERE storage_id = 3 AND resource_id = 7);

INSERT INTO StorageResources (storage_id, resource_id, quantity)
SELECT 3, 8, 40 WHERE NOT EXISTS (SELECT 1 FROM StorageResources WHERE storage_id = 3 AND resource_id = 8);

INSERT INTO StorageResources (storage_id, resource_id, quantity)
SELECT 3, 9, 900 WHERE NOT EXISTS (SELECT 1 FROM StorageResources WHERE storage_id = 3 AND resource_id = 9);

INSERT INTO Departments (id, facility_id, name, description, email, phone)
SELECT 1, 1, 'Cardiology', 'Heart and cardiovascular care', 'cardiology@vixhealth.com', '+390210000001'
WHERE NOT EXISTS (SELECT 1 FROM Departments WHERE id = 1);

INSERT INTO Departments (id, facility_id, name, description, email, phone)
SELECT 2, 2, 'Neurology', 'Brain and nervous system care', 'neurology@vixhealth.com', '+390210000002'
WHERE NOT EXISTS (SELECT 1 FROM Departments WHERE id = 2);

INSERT INTO Departments (id, facility_id, name, description, email, phone)
SELECT 3, 3, 'Radiology', 'Imaging and diagnostic services', 'radiology@vixhealth.com', '+390210000003'
WHERE NOT EXISTS (SELECT 1 FROM Departments WHERE id = 3);

INSERT INTO Departments (id, facility_id, name, description, email, phone)
SELECT 4, 1, 'Administration', 'Administrative department', 'admin.hospital@vixhealth.com', '+390210000004'
WHERE NOT EXISTS (SELECT 1 FROM Departments WHERE id = 4);

INSERT INTO Departments (id, facility_id, name, description, email, phone)
SELECT 5, 2, 'Administration', 'Administrative department', 'admin.north@vixhealth.com', '+390210000005'
WHERE NOT EXISTS (SELECT 1 FROM Departments WHERE id = 5);

INSERT INTO Departments (id, facility_id, name, description, email, phone)
SELECT 6, 3, 'Administration', 'Administrative department', 'admin.diagnostic@vixhealth.com', '+390210000006'
WHERE NOT EXISTS (SELECT 1 FROM Departments WHERE id = 6);

INSERT INTO Employees (id, type, name, surname, birth_date, birth_place, gender, email, phone, hire_date, firebase_uid, department_id, specialty, license_number, secretary_type)
SELECT 1, 'MEDICAL_SPECIALIST', 'Marco', 'Rossi', '1980-03-12', 'Milano', 'M', 'marco.rossi@vixhealth.com', '+393331111111', '2015-09-01', NULL, 1, 'Cardiology', 'LIC-CARD-001', NULL
WHERE NOT EXISTS (SELECT 1 FROM Employees WHERE id = 1);

INSERT INTO Employees (id, type, name, surname, birth_date, birth_place, gender, email, phone, hire_date, firebase_uid, department_id, specialty, license_number, secretary_type)
SELECT 2, 'MEDICAL_SPECIALIST', 'Elena', 'Bianchi', '1985-07-22', 'Torino', 'F', 'elena.bianchi@vixhealth.com', '+393332222222', '2017-04-15', NULL, 2, 'Neurology', 'LIC-NEUR-002', NULL
WHERE NOT EXISTS (SELECT 1 FROM Employees WHERE id = 2);

INSERT INTO Employees (id, type, name, surname, birth_date, birth_place, gender, email, phone, hire_date, firebase_uid, department_id, specialty, license_number, secretary_type)
SELECT 3, 'MEDICAL_SPECIALIST', 'Luca', 'Verdi', '1978-11-05', 'Roma', 'M', 'luca.verdi@vixhealth.com', '+393333333333', '2012-01-20', NULL, 3, 'Radiology', 'LIC-RAD-003', NULL
WHERE NOT EXISTS (SELECT 1 FROM Employees WHERE id = 3);

INSERT INTO Employees (id, type, name, surname, birth_date, birth_place, gender, email, phone, hire_date, firebase_uid, department_id, specialty, license_number, secretary_type)
SELECT 4, 'SECRETARY', 'Sara', 'Conti', '1990-05-10', 'Milano', 'F', 'sara.conti@vixhealth.com', '+393334444444', '2020-02-01', NULL, 4, NULL, NULL, 'Front Office'
WHERE NOT EXISTS (SELECT 1 FROM Employees WHERE id = 4);

INSERT INTO Employees (id, type, name, surname, birth_date, birth_place, gender, email, phone, hire_date, firebase_uid, department_id, specialty, license_number, secretary_type)
SELECT 5, 'SECRETARY', 'Paolo', 'Ferrari', '1988-09-14', 'Bergamo', 'M', 'paolo.ferrari@vixhealth.com', '+393335555555', '2019-06-10', NULL, 5, NULL, NULL, 'Admissions'
WHERE NOT EXISTS (SELECT 1 FROM Employees WHERE id = 5);

INSERT INTO Employees (id, type, name, surname, birth_date, birth_place, gender, email, phone, hire_date, firebase_uid, department_id, specialty, license_number, secretary_type)
SELECT 6, 'TECHNICIAN', 'Giulia', 'Romano', '1992-12-01', 'Como', 'F', 'giulia.romano@vixhealth.com', '+393336666666', '2021-03-18', NULL, 4, NULL, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM Employees WHERE id = 6);

INSERT INTO Employees (id, type, name, surname, birth_date, birth_place, gender, email, phone, hire_date, firebase_uid, department_id, specialty, license_number, secretary_type)
SELECT 7, 'TECHNICIAN', 'Andrea', 'Gallo', '1987-02-25', 'Pavia', 'M', 'andrea.gallo@vixhealth.com', '+393337777777', '2018-11-05', NULL, 6, NULL, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM Employees WHERE id = 7);

INSERT INTO Employees (id, type, name, surname, birth_date, birth_place, gender, email, phone, hire_date, firebase_uid, department_id, specialty, license_number, secretary_type)
SELECT 8, 'BUYER', 'Francesca', 'Marini', '1991-04-30', 'Milano', 'F', 'francesca.marini@vixhealth.com', '+393338888888', '2022-01-12', NULL, 6, NULL, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM Employees WHERE id = 8);

INSERT INTO Employees (id, type, name, surname, birth_date, birth_place, gender, email, phone, hire_date, firebase_uid, department_id, specialty, license_number, secretary_type)
SELECT 9, 'BUYER', 'Davide', 'Lombardi', '1989-08-08', 'Varese', 'M', 'davide.lombardi@vixhealth.com', '+393339999999', '2021-10-01', NULL, 5, NULL, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM Employees WHERE id = 9);

INSERT INTO Employees (id, type, name, surname, birth_date, birth_place, gender, email, phone, hire_date, firebase_uid, department_id, specialty, license_number, secretary_type)
SELECT 10, 'STAFF_MANAGER', 'Alessia', 'Moretti', '1983-06-16', 'Milano', 'F', 'alessia.moretti@vixhealth.com', '+393330000001', '2016-07-01', NULL, 4, NULL, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM Employees WHERE id = 10);

INSERT INTO Employees (id, type, name, surname, birth_date, birth_place, gender, email, phone, hire_date, firebase_uid, department_id, specialty, license_number, secretary_type)
SELECT 11, 'STAFF_MANAGER', 'Matteo', 'Ricci', '1981-01-19', 'Torino', 'M', 'matteo.ricci@vixhealth.com', '+393330000002', '2014-05-20', NULL, 5, NULL, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM Employees WHERE id = 11);

INSERT INTO Rooms (id, facility_id, room_number, type, specialization, beds_count, employee_id)
SELECT 1, 1, 'H-101', 'INTERNATION_ROOM', NULL, 3, NULL
WHERE NOT EXISTS (SELECT 1 FROM Rooms WHERE id = 1);

INSERT INTO Rooms (id, facility_id, room_number, type, specialization, beds_count, employee_id)
SELECT 2, 1, 'H-102', 'INTERNATION_ROOM', NULL, 2, NULL
WHERE NOT EXISTS (SELECT 1 FROM Rooms WHERE id = 2);

INSERT INTO Rooms (id, facility_id, room_number, type, specialization, beds_count, employee_id)
SELECT 3, 1, 'H-O1', 'OFFICE', NULL, NULL, 4
WHERE NOT EXISTS (SELECT 1 FROM Rooms WHERE id = 3);

INSERT INTO Rooms (id, facility_id, room_number, type, specialization, beds_count, employee_id)
SELECT 4, 1, 'H-O2', 'OFFICE', NULL, NULL, 10
WHERE NOT EXISTS (SELECT 1 FROM Rooms WHERE id = 4);

INSERT INTO Rooms (id, facility_id, room_number, type, specialization, beds_count, employee_id)
SELECT 5, 1, 'H-S1', 'SPECIALIZED_ROOM', 'Surgery', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM Rooms WHERE id = 5);

INSERT INTO Rooms (id, facility_id, room_number, type, specialization, beds_count, employee_id)
SELECT 6, 1, 'H-S2', 'SPECIALIZED_ROOM', 'Radiology', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM Rooms WHERE id = 6);

INSERT INTO Rooms (id, facility_id, room_number, type, specialization, beds_count, employee_id)
SELECT 7, 2, 'C-O1', 'OFFICE', NULL, NULL, 5
WHERE NOT EXISTS (SELECT 1 FROM Rooms WHERE id = 7);

INSERT INTO Rooms (id, facility_id, room_number, type, specialization, beds_count, employee_id)
SELECT 8, 2, 'C-S1', 'SPECIALIZED_ROOM', 'MRI', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM Rooms WHERE id = 8);

INSERT INTO Rooms (id, facility_id, room_number, type, specialization, beds_count, employee_id)
SELECT 9, 2, 'C-S2', 'SPECIALIZED_ROOM', 'Laboratory', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM Rooms WHERE id = 9);

INSERT INTO Rooms (id, facility_id, room_number, type, specialization, beds_count, employee_id)
SELECT 10, 3, 'D-O1', 'OFFICE', NULL, NULL, 8
WHERE NOT EXISTS (SELECT 1 FROM Rooms WHERE id = 10);

INSERT INTO Rooms (id, facility_id, room_number, type, specialization, beds_count, employee_id)
SELECT 11, 3, 'D-S1', 'SPECIALIZED_ROOM', 'CT Scan', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM Rooms WHERE id = 11);

INSERT INTO Rooms (id, facility_id, room_number, type, specialization, beds_count, employee_id)
SELECT 12, 3, 'D-S2', 'SPECIALIZED_ROOM', 'Ultrasound', NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM Rooms WHERE id = 12);

INSERT INTO Patients (id, fiscal_code, name, surname, birth_date, birth_place, gender, email, phone)
SELECT 1, 'RSSMRA80A01F205X', 'Mario', 'Rossi', '1980-01-01', 'Milano', 'M', 'mario.rossi@email.com', '+393441111111'
WHERE NOT EXISTS (SELECT 1 FROM Patients WHERE id = 1);

INSERT INTO Patients (id, fiscal_code, name, surname, birth_date, birth_place, gender, email, phone)
SELECT 2, 'BNCLRA92B41L219Y', 'Laura', 'Bianchi', '1992-02-10', 'Torino', 'F', 'laura.bianchi@email.com', '+393442222222'
WHERE NOT EXISTS (SELECT 1 FROM Patients WHERE id = 2);

INSERT INTO Patients (id, fiscal_code, name, surname, birth_date, birth_place, gender, email, phone)
SELECT 3, 'VRDGPP75C12H501Z', 'Giuseppe', 'Verdi', '1975-03-12', 'Roma', 'M', 'giuseppe.verdi@email.com', '+393443333333'
WHERE NOT EXISTS (SELECT 1 FROM Patients WHERE id = 3);

INSERT INTO Patients (id, fiscal_code, name, surname, birth_date, birth_place, gender, email, phone)
SELECT 4, 'CNTCHR88D55F205A', 'Chiara', 'Conti', '1988-04-20', 'Milano', 'F', 'chiara.conti@email.com', '+393444444444'
WHERE NOT EXISTS (SELECT 1 FROM Patients WHERE id = 4);

INSERT INTO Patients (id, fiscal_code, name, surname, birth_date, birth_place, gender, email, phone)
SELECT 5, 'FRRLCU01E22D969B', 'Luca', 'Ferrari', '2001-05-22', 'Genova', 'M', 'luca.ferrari@email.com', '+393445555555'
WHERE NOT EXISTS (SELECT 1 FROM Patients WHERE id = 5);

INSERT INTO MedicalRecords (id, patient_id, height, weight, blood_type, allergies, vaccines)
SELECT 1, 1, 178, 75, 'A+', 'Penicillin', 'Tetanus, COVID-19'
WHERE NOT EXISTS (SELECT 1 FROM MedicalRecords WHERE id = 1);

INSERT INTO MedicalRecords (id, patient_id, height, weight, blood_type, allergies, vaccines)
SELECT 2, 2, 165, 58, 'O-', 'Dust', 'COVID-19'
WHERE NOT EXISTS (SELECT 1 FROM MedicalRecords WHERE id = 2);

INSERT INTO MedicalRecords (id, patient_id, height, weight, blood_type, allergies, vaccines)
SELECT 3, 3, 182, 84, 'B+', 'None', 'Tetanus'
WHERE NOT EXISTS (SELECT 1 FROM MedicalRecords WHERE id = 3);

INSERT INTO MedicalRecords (id, patient_id, height, weight, blood_type, allergies, vaccines)
SELECT 4, 4, 170, 63, 'AB+', 'Peanuts', 'COVID-19, Hepatitis B'
WHERE NOT EXISTS (SELECT 1 FROM MedicalRecords WHERE id = 4);

INSERT INTO MedicalRecords (id, patient_id, height, weight, blood_type, allergies, vaccines)
SELECT 5, 5, 176, 70, 'A-', 'Latex', 'Tetanus, COVID-19'
WHERE NOT EXISTS (SELECT 1 FROM MedicalRecords WHERE id = 5);

INSERT INTO MedicalConditions (id, medical_record_id, name, diagnosis_date, type, description, treatment)
SELECT 1, 1, 'Hypertension', '2023-01-12', 'Cardiovascular', 'High blood pressure', 'Lifestyle changes and medication'
WHERE NOT EXISTS (SELECT 1 FROM MedicalConditions WHERE id = 1);

INSERT INTO MedicalConditions (id, medical_record_id, name, diagnosis_date, type, description, treatment)
SELECT 2, 2, 'Migraine', '2022-06-18', 'Neurological', 'Recurring migraine episodes', 'Pain management'
WHERE NOT EXISTS (SELECT 1 FROM MedicalConditions WHERE id = 2);

INSERT INTO MedicalConditions (id, medical_record_id, name, diagnosis_date, type, description, treatment)
SELECT 3, 2, 'Anemia', '2023-03-09', 'Hematologic', 'Low iron levels', 'Iron supplements'
WHERE NOT EXISTS (SELECT 1 FROM MedicalConditions WHERE id = 3);

INSERT INTO MedicalConditions (id, medical_record_id, name, diagnosis_date, type, description, treatment)
SELECT 4, 4, 'Asthma', '2020-11-02', 'Respiratory', 'Mild persistent asthma', 'Inhaler therapy'
WHERE NOT EXISTS (SELECT 1 FROM MedicalConditions WHERE id = 4);

INSERT INTO MedicalConditions (id, medical_record_id, name, diagnosis_date, type, description, treatment)
SELECT 5, 4, 'Allergic Rhinitis', '2021-04-15', 'Allergy', 'Seasonal allergies', 'Antihistamines'
WHERE NOT EXISTS (SELECT 1 FROM MedicalConditions WHERE id = 5);

INSERT INTO MedicalConditions (id, medical_record_id, name, diagnosis_date, type, description, treatment)
SELECT 6, 4, 'Dermatitis', '2023-08-10', 'Dermatologic', 'Skin irritation', 'Topical cream'
WHERE NOT EXISTS (SELECT 1 FROM MedicalConditions WHERE id = 6);

INSERT INTO MedicalConditions (id, medical_record_id, name, diagnosis_date, type, description, treatment)
SELECT 7, 5, 'Sprained Ankle', '2024-02-20', 'Orthopedic', 'Sports injury', 'Rest and physiotherapy'
WHERE NOT EXISTS (SELECT 1 FROM MedicalConditions WHERE id = 7);

INSERT INTO Prescriptions (id, medical_record_id, medical_specialist_id, prescription_date, medication)
SELECT 1, 1, 1, '2024-01-10 10:00:00', 'Amlodipine 5mg'
WHERE NOT EXISTS (SELECT 1 FROM Prescriptions WHERE id = 1);

INSERT INTO Prescriptions (id, medical_record_id, medical_specialist_id, prescription_date, medication)
SELECT 2, 3, 2, '2024-02-11 11:30:00', 'Ibuprofen 400mg'
WHERE NOT EXISTS (SELECT 1 FROM Prescriptions WHERE id = 2);

INSERT INTO Prescriptions (id, medical_record_id, medical_specialist_id, prescription_date, medication)
SELECT 3, 3, 2, '2024-03-05 09:45:00', 'Vitamin B Complex'
WHERE NOT EXISTS (SELECT 1 FROM Prescriptions WHERE id = 3);

INSERT INTO Prescriptions (id, medical_record_id, medical_specialist_id, prescription_date, medication)
SELECT 4, 4, 1, '2024-04-12 15:00:00', 'Salbutamol Inhaler'
WHERE NOT EXISTS (SELECT 1 FROM Prescriptions WHERE id = 4);

INSERT INTO Prescriptions (id, medical_record_id, medical_specialist_id, prescription_date, medication)
SELECT 5, 5, 3, '2024-05-01 12:00:00', 'Paracetamol 500mg'
WHERE NOT EXISTS (SELECT 1 FROM Prescriptions WHERE id = 5);

INSERT INTO Prescriptions (id, medical_record_id, medical_specialist_id, prescription_date, medication)
SELECT 6, 5, 3, '2024-05-18 14:20:00', 'Diclofenac Gel'
WHERE NOT EXISTS (SELECT 1 FROM Prescriptions WHERE id = 6);

INSERT INTO Prescriptions (id, medical_record_id, medical_specialist_id, prescription_date, medication)
SELECT 7, 5, 1, '2024-06-02 08:30:00', 'Cetirizine 10mg'
WHERE NOT EXISTS (SELECT 1 FROM Prescriptions WHERE id = 7);

INSERT INTO Surgeries (id, medical_record_id, specialized_room_id, surgery_date, name, description)
SELECT 1, 2, 5, '2024-03-20 08:00:00', 'Appendectomy', 'Appendix removal surgery'
WHERE NOT EXISTS (SELECT 1 FROM Surgeries WHERE id = 1);

INSERT INTO Surgeries (id, medical_record_id, specialized_room_id, surgery_date, name, description)
SELECT 2, 3, 5, '2024-04-01 09:00:00', 'Knee Arthroscopy', 'Minimally invasive knee procedure'
WHERE NOT EXISTS (SELECT 1 FROM Surgeries WHERE id = 2);

INSERT INTO Surgeries (id, medical_record_id, specialized_room_id, surgery_date, name, description)
SELECT 3, 3, 6, '2024-04-18 10:30:00', 'Diagnostic Imaging Procedure', 'Radiology-supported surgical assessment'
WHERE NOT EXISTS (SELECT 1 FROM Surgeries WHERE id = 3);

INSERT INTO Surgeries (id, medical_record_id, specialized_room_id, surgery_date, name, description)
SELECT 4, 5, 5, '2024-06-10 07:45:00', 'Ankle Ligament Repair', 'Repair of damaged ankle ligament'
WHERE NOT EXISTS (SELECT 1 FROM Surgeries WHERE id = 4);

INSERT INTO Machines (id, specialized_room_id, name, status)
SELECT 1, 5, 'Surgical Robot A1', 'WORKING'
WHERE NOT EXISTS (SELECT 1 FROM Machines WHERE id = 1);

INSERT INTO Machines (id, specialized_room_id, name, status)
SELECT 2, 6, 'X-Ray Machine XR-200', 'UNDER_MAINTENANCE'
WHERE NOT EXISTS (SELECT 1 FROM Machines WHERE id = 2);

INSERT INTO Machines (id, specialized_room_id, name, status)
SELECT 3, 8, 'MRI Scanner M-300', 'WORKING'
WHERE NOT EXISTS (SELECT 1 FROM Machines WHERE id = 3);

INSERT INTO Machines (id, specialized_room_id, name, status)
SELECT 4, 11, 'CT Scanner CT-500', 'FAULTY'
WHERE NOT EXISTS (SELECT 1 FROM Machines WHERE id = 4);

INSERT INTO RoomPatients (room_id, patient_id)
SELECT 1, 2 WHERE NOT EXISTS (SELECT 1 FROM RoomPatients WHERE room_id = 1 AND patient_id = 2);

INSERT INTO RoomPatients (room_id, patient_id)
SELECT 1, 3 WHERE NOT EXISTS (SELECT 1 FROM RoomPatients WHERE room_id = 1 AND patient_id = 3);

INSERT INTO RoomPatients (room_id, patient_id)
SELECT 2, 5 WHERE NOT EXISTS (SELECT 1 FROM RoomPatients WHERE room_id = 2 AND patient_id = 5);
