-- =====================================================
-- SEED DATA
-- =====================================================

INSERT INTO MedicalFacilities (id, name, type, latitude, longitude, email, phone) VALUES
                                                                                      (1, 'VIX Central Hospital', 'HOSPITAL', 45.4642, 9.1900, 'central@vixhealth.com', '+390200000001'),
                                                                                      (2, 'VIX North Clinic', 'MEDICAL_FACILITY', 45.5000, 9.2100, 'north@vixhealth.com', '+390200000002'),
                                                                                      (3, 'VIX Diagnostic Center', 'MEDICAL_FACILITY', 45.4300, 9.1700, 'diagnostic@vixhealth.com', '+390200000003');

INSERT INTO Storages (id, facility_id) VALUES
                                           (1, 1),
                                           (2, 2),
                                           (3, 3);

INSERT INTO Resources (id, name, description, price) VALUES
                                                         (1, 'Syringes', 'Disposable sterile syringes', 0.50),
                                                         (2, 'Gloves', 'Disposable medical gloves', 0.20),
                                                         (3, 'Bandages', 'Sterile bandages', 1.00),
                                                         (4, 'Masks', 'Surgical masks', 0.30),
                                                         (5, 'Disinfectant', 'Medical surface disinfectant', 4.50),
                                                         (6, 'Gauze', 'Sterile gauze pads', 0.80),
                                                         (7, 'IV Bags', 'Intravenous fluid bags', 3.20),
                                                         (8, 'Thermometers', 'Digital thermometers', 12.00),
                                                         (9, 'Test Tubes', 'Laboratory test tubes', 0.40);

INSERT INTO StorageResources (storage_id, resource_id, quantity) VALUES
                                                                     (1, 1, 500),
                                                                     (1, 2, 1000),
                                                                     (1, 3, 250),
                                                                     (2, 4, 600),
                                                                     (2, 5, 80),
                                                                     (2, 6, 300),
                                                                     (3, 7, 120),
                                                                     (3, 8, 40),
                                                                     (3, 9, 900);

INSERT INTO Departments (id, facility_id, name, description, email, phone) VALUES
                                                                               (1, 1, 'Cardiology', 'Heart and cardiovascular care', 'cardiology@vixhealth.com', '+390210000001'),
                                                                               (2, 2, 'Neurology', 'Brain and nervous system care', 'neurology@vixhealth.com', '+390210000002'),
                                                                               (3, 3, 'Radiology', 'Imaging and diagnostic services', 'radiology@vixhealth.com', '+390210000003'),
                                                                               (4, 1, 'Administration', 'Administrative department', 'admin.hospital@vixhealth.com', '+390210000004'),
                                                                               (5, 2, 'Administration', 'Administrative department', 'admin.north@vixhealth.com', '+390210000005'),
                                                                               (6, 3, 'Administration', 'Administrative department', 'admin.diagnostic@vixhealth.com', '+390210000006');

INSERT INTO Employees (id, type, name, surname, birth_date, birth_place, gender, email, phone, hire_date, firebase_uid, department_id, specialty, license_number, secretary_type) VALUES
                                                                                                                                                                        (1, 'MEDICAL_SPECIALIST', 'Marco', 'Rossi', '1980-03-12', 'Milano', 'M', 'marco.rossi@vixhealth.com', '+393331111111', '2015-09-01', 'k1bFTJFXoPNkGase8Of29UqBEef2', 1, 'Cardiology', 'LIC-CARD-001', NULL),
                                                                                                                                                                        (2, 'MEDICAL_SPECIALIST', 'Elena', 'Bianchi', '1985-07-22', 'Torino', 'F', 'elena.bianchi@vixhealth.com', '+393332222222', '2017-04-15', 'mPgpiVHtIoeDdF3ozIYfygOT94v2', 2, 'Neurology', 'LIC-NEUR-002', NULL),
                                                                                                                                                                        (3, 'MEDICAL_SPECIALIST', 'Luca', 'Verdi', '1978-11-05', 'Roma', 'M', 'luca.verdi@vixhealth.com', '+393333333333', '2012-01-20', 'tj0Z1CfPzXYRCJgQ2erml1xclA92', 3, 'Radiology', 'LIC-RAD-003', NULL),

                                                                                                                                                                        (4, 'SECRETARY', 'Sara', 'Conti', '1990-05-10', 'Milano', 'F', 'sara.conti@vixhealth.com', '+393334444444', '2020-02-01', 'JvmV0VMIehPndiGUpuZalPeUMRW2', 4, NULL, NULL, 'Front Office'),
                                                                                                                                                                        (5, 'SECRETARY', 'Paolo', 'Ferrari', '1988-09-14', 'Bergamo', 'M', 'paolo.ferrari@vixhealth.com', '+393335555555', '2019-06-10', 'IXRvZKUD5ETDQtNp2yAFlI5ibXC3', 5, NULL, NULL, 'Admissions'),

                                                                                                                                                                        (6, 'TECHNICIAN', 'Giulia', 'Romano', '1992-12-01', 'Como', 'F', 'giulia.romano@vixhealth.com', '+393336666666', '2021-03-18', 'ur2cUN0ZElNQza55oOI4eHXxV1J3', 4, NULL, NULL, NULL),
                                                                                                                                                                        (7, 'TECHNICIAN', 'Andrea', 'Gallo', '1987-02-25', 'Pavia', 'M', 'andrea.gallo@vixhealth.com', '+393337777777', '2018-11-05', 'qn4n2l8vJfNrO6tPaLlTNu6WzA23', 6, NULL, NULL, NULL),

                                                                                                                                                                        (8, 'BUYER', 'Francesca', 'Marini', '1991-04-30', 'Milano', 'F', 'francesca.marini@vixhealth.com', '+393338888888', '2022-01-12', 'lDLRs4R24XgV83CCivm3JLnr9ul1', 6, NULL, NULL, NULL),
                                                                                                                                                                        (9, 'BUYER', 'Davide', 'Lombardi', '1989-08-08', 'Varese', 'M', 'davide.lombardi@vixhealth.com', '+393339999999', '2021-10-01', 'StChNgVRQ3NkBiVCGuxBIoFd5Hw1', 5, NULL, NULL, NULL),

                                                                                                                                                                        (10, 'STAFF_MANAGER', 'Alessia', 'Moretti', '1983-06-16', 'Milano', 'F', 'alessia.moretti@vixhealth.com', '+393330000001', '2016-07-01', 'Ae0rqqKiKJZxB972yfvs2JLwkcI2', 4, NULL, NULL, NULL),
                                                                                                                                                                        (11, 'STAFF_MANAGER', 'Matteo', 'Ricci', '1981-01-19', 'Torino', 'M', 'matteo.ricci@vixhealth.com', '+393330000002', '2014-05-20', 't23acUiIipcsK40VMjihNtHLEbi1', 5, NULL, NULL, NULL);

INSERT INTO Rooms (id, facility_id, room_number, type, specialization, beds_count, employee_id) VALUES
                                                                                                    (1, 1, 'H-101', 'INTERNATION_ROOM', NULL, 3, NULL),
                                                                                                    (2, 1, 'H-102', 'INTERNATION_ROOM', NULL, 2, NULL),
                                                                                                    (3, 1, 'H-O1', 'OFFICE', NULL, NULL, 4),
                                                                                                    (4, 1, 'H-O2', 'OFFICE', NULL, NULL, 10),
                                                                                                    (5, 1, 'H-S1', 'SPECIALIZED_ROOM', 'Surgery', NULL, NULL),
                                                                                                    (6, 1, 'H-S2', 'SPECIALIZED_ROOM', 'Radiology', NULL, NULL),

                                                                                                    (7, 2, 'C-O1', 'OFFICE', NULL, NULL, 5),
                                                                                                    (8, 2, 'C-S1', 'SPECIALIZED_ROOM', 'MRI', NULL, NULL),
                                                                                                    (9, 2, 'C-S2', 'SPECIALIZED_ROOM', 'Laboratory', NULL, NULL),

                                                                                                    (10, 3, 'D-O1', 'OFFICE', NULL, NULL, 8),
                                                                                                    (11, 3, 'D-S1', 'SPECIALIZED_ROOM', 'CT Scan', NULL, NULL),
                                                                                                    (12, 3, 'D-S2', 'SPECIALIZED_ROOM', 'Ultrasound', NULL, NULL);

INSERT INTO Patients (id, fiscal_code, name, surname, birth_date, birth_place, gender, email, phone) VALUES
                                                                                                         (1, 'RSSMRA80A01F205X', 'Mario', 'Rossi', '1980-01-01', 'Milano', 'M', 'mario.rossi@email.com', '+393441111111'),
                                                                                                         (2, 'BNCLRA92B41L219Y', 'Laura', 'Bianchi', '1992-02-10', 'Torino', 'F', 'laura.bianchi@email.com', '+393442222222'),
                                                                                                         (3, 'VRDGPP75C12H501Z', 'Giuseppe', 'Verdi', '1975-03-12', 'Roma', 'M', 'giuseppe.verdi@email.com', '+393443333333'),
                                                                                                         (4, 'CNTCHR88D55F205A', 'Chiara', 'Conti', '1988-04-20', 'Milano', 'F', 'chiara.conti@email.com', '+393444444444'),
                                                                                                         (5, 'FRRLCU01E22D969B', 'Luca', 'Ferrari', '2001-05-22', 'Genova', 'M', 'luca.ferrari@email.com', '+393445555555');

INSERT INTO MedicalRecords (id, patient_id, height, weight, blood_type, allergies, vaccines) VALUES
                                                                                                 (1, 1, 178, 75, 'A+', 'Penicillin', 'Tetanus, COVID-19'),
                                                                                                 (2, 2, 165, 58, 'O-', 'Dust', 'COVID-19'),
                                                                                                 (3, 3, 182, 84, 'B+', 'None', 'Tetanus'),
                                                                                                 (4, 4, 170, 63, 'AB+', 'Peanuts', 'COVID-19, Hepatitis B'),
                                                                                                 (5, 5, 176, 70, 'A-', 'Latex', 'Tetanus, COVID-19');

INSERT INTO MedicalConditions (id, medical_record_id, name, diagnosis_date, type, description, treatment) VALUES
                                                                                                              (1, 1, 'Hypertension', '2023-01-12', 'Cardiovascular', 'High blood pressure', 'Lifestyle changes and medication'),

                                                                                                              (2, 2, 'Migraine', '2022-06-18', 'Neurological', 'Recurring migraine episodes', 'Pain management'),
                                                                                                              (3, 2, 'Anemia', '2023-03-09', 'Hematologic', 'Low iron levels', 'Iron supplements'),

                                                                                                              (4, 4, 'Asthma', '2020-11-02', 'Respiratory', 'Mild persistent asthma', 'Inhaler therapy'),
                                                                                                              (5, 4, 'Allergic Rhinitis', '2021-04-15', 'Allergy', 'Seasonal allergies', 'Antihistamines'),
                                                                                                              (6, 4, 'Dermatitis', '2023-08-10', 'Dermatologic', 'Skin irritation', 'Topical cream'),

                                                                                                              (7, 5, 'Sprained Ankle', '2024-02-20', 'Orthopedic', 'Sports injury', 'Rest and physiotherapy');

INSERT INTO Prescriptions (id, medical_record_id, medical_specialist_id, prescription_date, medication) VALUES
                                                                                                            (1, 1, 1, '2024-01-10 10:00:00', 'Amlodipine 5mg'),

                                                                                                            (2, 3, 2, '2024-02-11 11:30:00', 'Ibuprofen 400mg'),
                                                                                                            (3, 3, 2, '2024-03-05 09:45:00', 'Vitamin B Complex'),

                                                                                                            (4, 4, 1, '2024-04-12 15:00:00', 'Salbutamol Inhaler'),

                                                                                                            (5, 5, 3, '2024-05-01 12:00:00', 'Paracetamol 500mg'),
                                                                                                            (6, 5, 3, '2024-05-18 14:20:00', 'Diclofenac Gel'),
                                                                                                            (7, 5, 1, '2024-06-02 08:30:00', 'Cetirizine 10mg');

INSERT INTO Surgeries (id, medical_record_id, specialized_room_id, surgery_date, name, description) VALUES
                                                                                                        (1, 2, 5, '2024-03-20 08:00:00', 'Appendectomy', 'Appendix removal surgery'),

                                                                                                        (2, 3, 5, '2024-04-01 09:00:00', 'Knee Arthroscopy', 'Minimally invasive knee procedure'),
                                                                                                        (3, 3, 6, '2024-04-18 10:30:00', 'Diagnostic Imaging Procedure', 'Radiology-supported surgical assessment'),

                                                                                                        (4, 5, 5, '2024-06-10 07:45:00', 'Ankle Ligament Repair', 'Repair of damaged ankle ligament');

INSERT INTO Machines (id, specialized_room_id, name, status) VALUES
                                                                 (1, 5, 'Surgical Robot A1', 'WORKING'),
                                                                 (2, 6, 'X-Ray Machine XR-200', 'UNDER_MAINTENANCE'),
                                                                 (3, 8, 'MRI Scanner M-300', 'WORKING'),
                                                                 (4, 11, 'CT Scanner CT-500', 'FAULTY');

INSERT INTO RoomPatients (room_id, patient_id) VALUES
                                                   (1, 2),
                                                   (1, 3),
                                                   (2, 5);

-- Reset identity sequences so new inserts don't collide with seeded IDs
ALTER TABLE MedicalFacilities ALTER COLUMN id RESTART WITH 4;
ALTER TABLE Storages           ALTER COLUMN id RESTART WITH 4;
ALTER TABLE Resources          ALTER COLUMN id RESTART WITH 10;
ALTER TABLE Departments        ALTER COLUMN id RESTART WITH 7;
ALTER TABLE Employees          ALTER COLUMN id RESTART WITH 12;
ALTER TABLE Rooms              ALTER COLUMN id RESTART WITH 13;
ALTER TABLE Patients           ALTER COLUMN id RESTART WITH 6;
ALTER TABLE MedicalRecords     ALTER COLUMN id RESTART WITH 6;
ALTER TABLE MedicalConditions  ALTER COLUMN id RESTART WITH 8;
ALTER TABLE Prescriptions      ALTER COLUMN id RESTART WITH 8;
ALTER TABLE Surgeries          ALTER COLUMN id RESTART WITH 5;
ALTER TABLE Machines           ALTER COLUMN id RESTART WITH 5;

