#include "SelectionPolicy.h"
#include "Facility.h"
#include <algorithm>
#include <stdexcept>
#include <limits>
#include <iostream>
NaiveSelection::NaiveSelection() : lastSelectedIndex(-1) {}
const FacilityType &NaiveSelection::selectFacility(const vector<FacilityType> &facilitiesOptions)
{
    if (facilitiesOptions.empty())
    {
        throw std::runtime_error("No facilities available for selection");
    }
    lastSelectedIndex = (lastSelectedIndex + 1) % facilitiesOptions.size();
    return facilitiesOptions[lastSelectedIndex];
}

const string NaiveSelection::toString() const
{
    return "nve";
}

NaiveSelection *NaiveSelection::clone() const
{
    return new NaiveSelection(*this);
}

BalancedSelection::BalancedSelection(int LifeQualityScore, int EconomyScore, int EnvironmentScore)
    : LifeQualityScore(LifeQualityScore), EconomyScore(EconomyScore), EnvironmentScore(EnvironmentScore) {}

const FacilityType &BalancedSelection::selectFacility(const vector<FacilityType> &facilitiesOptions)
{
    if (facilitiesOptions.empty())
    {
        throw std::runtime_error("Error: No facilities available for selection in BalancedSelection.");
    }

    int bestIndex = 0;                                   // To store the index of the best facility
    int minDifference = std::numeric_limits<int>::max(); // Initialize with the largest possible value

    for (size_t i = 0; i < facilitiesOptions.size(); i++)
    {
        const FacilityType &facility = facilitiesOptions[i];

        // Calculate scores after considering the impact of the current facility
        int maxScore = std::max({LifeQualityScore + facility.getLifeQualityScore(),
                                 EconomyScore + facility.getEconomyScore(),
                                 EnvironmentScore + facility.getEnvironmentScore()});
        int minScore = std::min({LifeQualityScore + facility.getLifeQualityScore(),
                                 EconomyScore + facility.getEconomyScore(),
                                 EnvironmentScore + facility.getEnvironmentScore()});
        int difference = maxScore - minScore;

        // Update the bestIndex if a smaller difference is found
        if (difference < minDifference)
        {
            minDifference = difference;
            bestIndex = i;
        }
    }

    LifeQualityScore=LifeQualityScore + facilitiesOptions[bestIndex].getLifeQualityScore();
    EconomyScore=EconomyScore + facilitiesOptions[bestIndex].getEconomyScore();
    EnvironmentScore += facilitiesOptions[bestIndex].getEnvironmentScore();
    // Return the facility with the best score balance
    return facilitiesOptions[bestIndex];
}

const string BalancedSelection::toString() const
{
    return "bal";
}

BalancedSelection *BalancedSelection::clone() const
{
    return new BalancedSelection(*this);
}

EconomySelection::EconomySelection() : lastSelectedIndex(-1)
{
}

const FacilityType &EconomySelection::selectFacility(const vector<FacilityType> &facilitiesOptions)
{
    for (size_t i = 0; i < facilitiesOptions.size(); ++i)
    {
        lastSelectedIndex = (lastSelectedIndex + 1) % facilitiesOptions.size();
        if (facilitiesOptions[lastSelectedIndex].getCategory() == FacilityCategory::ECONOMY)
        {
            return facilitiesOptions[lastSelectedIndex];
        }
    }
    throw std::runtime_error("No facilities in the Economy category available for selection");
}

const string EconomySelection::toString() const
{
    return "eco";
}

EconomySelection *EconomySelection::clone() const
{
    return new EconomySelection(*this);
}

SustainabilitySelection::SustainabilitySelection() : lastSelectedIndex(-1)
{
}

const FacilityType &SustainabilitySelection::selectFacility(const vector<FacilityType> &facilitiesOptions)
{
    for (size_t i = 0; i < facilitiesOptions.size(); ++i)
    {
        lastSelectedIndex = (lastSelectedIndex + 1) % facilitiesOptions.size();
        if (facilitiesOptions[lastSelectedIndex].getCategory() == FacilityCategory::ENVIRONMENT)
        {
            return facilitiesOptions[lastSelectedIndex];
        }
    }
    throw std::runtime_error("No facilities in the Sustainability category available for selection");
}

const string SustainabilitySelection::toString() const
{
    return "env";
}

SustainabilitySelection *SustainabilitySelection::clone() const
{
    return new SustainabilitySelection(*this);
}