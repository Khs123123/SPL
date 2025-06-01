#pragma once
#include <string>
#include <vector>
#include "Facility.h"
#include "Plan.h"
#include "Settlement.h"
using std::string;
using std::vector;

class BaseAction;
class SelectionPolicy;
// TODO:CHECK THE ROLE OF FIVE
class Simulation
{
public:
    // Rule of Five
    ~Simulation();                                  // Destructor
    Simulation(const Simulation &other);            // Copy Constructor
    Simulation &operator=(const Simulation &other); // Copy Assignment Operator
        Simulation(Simulation &&other) noexcept;   // Move Constructor
    Simulation& operator=(Simulation &&other) noexcept; // Move Assignment Operator

    Simulation(const string &configFilePath);
    void start();
    void addPlan(const Settlement &settlement, SelectionPolicy *selectionPolicy);
    void addAction(BaseAction *action);
    bool addSettlement(Settlement *settlement);
    bool addFacility(FacilityType facility);
    bool isSettlementExists(const string &settlementName);
    Settlement *getSettlement(const string &settlementName);
    Plan &getPlan(const int planID);
    void step();
    void close();
    void open();
    // Accessor for actionsLog
    const vector<BaseAction *> &getActionsLog() const;
    const vector<Plan> &getPlans() const;

private:
    bool isRunning;
    int planCounter; // For assigning unique plan IDs
    vector<BaseAction *> actionsLog;
    vector<Plan> plans;
    vector<Settlement *> settlements;
    vector<FacilityType> facilitiesOptions;
};