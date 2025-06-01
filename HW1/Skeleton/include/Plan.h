#pragma once
#include <vector>
#include "Facility.h"
#include "Settlement.h"
#include "SelectionPolicy.h"
using std::vector;

enum class PlanStatus
{
    AVALIABLE,
    BUSY,
};
// TODO:CHECK THE ROLE OF FIVE
class Plan
{
public:
    Plan(const Settlement &settlement, const Plan &other);


    Plan &operator=(const Plan &other)=delete;
    Plan(const Plan &other); // copy constractor
    ~Plan();
    Plan(const int planId, const Settlement &settlement, SelectionPolicy *selectionPolicy, const vector<FacilityType> &facilityOptions);
    const int getlifeQualityScore() const;
    const int getEconomyScore() const;
    const int getEnvironmentScore() const;
    void setSelectionPolicy(SelectionPolicy *selectionPolicy);
    void step();
    void printStatus();
    const vector<Facility *> &getFacilities() const;
    void addFacility(Facility *facility);
    const string toString() const;
    const vector<FacilityType> getfacilityOptions() const;
    const SelectionPolicy *getSelectionPolicy() const;
    const int getPlanID() const;                  // To get the plan ID
    const Settlement &getSettlement() const; // To get the settlement
private:
    int plan_id;
    //const Settlement *settlementpoint; 

    const Settlement &settlement;
    SelectionPolicy *selectionPolicy; // What happens if we change this to a reference?
    PlanStatus status;
    vector<Facility *> facilities;
    vector<Facility *> underConstruction;
    const vector<FacilityType> &facilityOptions;
    int life_quality_score, economy_score, environment_score;
};
